package com.swp.PodBookingSystem.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.swp.PodBookingSystem.dto.request.Account.AccountCreationRequest;
import com.swp.PodBookingSystem.dto.request.Authentication.AuthenticationRequest;
import com.swp.PodBookingSystem.dto.request.Authentication.ForgotPasswordRequest;
import com.swp.PodBookingSystem.dto.request.Authentication.LogoutRequest;
import com.swp.PodBookingSystem.dto.request.Authentication.RefreshTokenRequest;
import com.swp.PodBookingSystem.dto.request.IntrospectRequest;
import com.swp.PodBookingSystem.dto.respone.AuthenticationResponse;
import com.swp.PodBookingSystem.dto.respone.IntrospectResponse;
import com.swp.PodBookingSystem.dto.respone.RefreshTokenResponse;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.entity.RefreshToken;
import com.swp.PodBookingSystem.exception.AppException;
import com.swp.PodBookingSystem.exception.ErrorCode;
import com.swp.PodBookingSystem.mapper.AccountMapper;
import com.swp.PodBookingSystem.repository.AccountRepository;
import com.swp.PodBookingSystem.repository.RefreshTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    AccountRepository accountRepository;
    RefreshTokenRepository refreshTokenRepository;
    AccountMapper accountMapper;
    SendEmailService sendEmailService;
    @NonFinal
    @Value("${jwt.JWT_SECRET_ACCESS_TOKEN}")
    protected String ACCESS_TOKEN_KEY;

    @NonFinal
    @Value("${jwt.JWT_SECRET_REFRESH_TOKEN}")
    protected String REFRESH_TOKEN_KEY;

    public IntrospectResponse decodeToken(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();
        JWSVerifier verifier = new MACVerifier(ACCESS_TOKEN_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);
        if (!(verified && expiryTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return IntrospectResponse.builder()
                .sub(signedJWT.getJWTClaimsSet().getSubject())
                .iss(signedJWT.getJWTClaimsSet().getIssuer())
                .accountId(signedJWT.getJWTClaimsSet().getClaim("accountId").toString())
                .exp(expiryTime.getTime())
                .iat(signedJWT.getJWTClaimsSet().getIssueTime().getTime())
                .build();

    }

    public AuthenticationResponse login(AuthenticationRequest request) throws ParseException {
        var account = accountRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXIST));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(),
                account.getPassword());
        if (!authenticated)
            throw new AppException(ErrorCode.INCORRECT_PASSWORD);

        var accessToken = generateAccessToken(account);
        var refreshToken = generateRefreshToken(account);
        SignedJWT decodeRefreshToken = SignedJWT.parse(refreshToken);
        refreshTokenRepository.save(new RefreshToken(null, refreshToken, account, decodeRefreshToken.getJWTClaimsSet().getIssueTime(), decodeRefreshToken.getJWTClaimsSet().getExpirationTime()));
        var accountResponse = accountMapper.toAccountResponseClient(account);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .account(accountResponse)
                .build();
    }

    @Transactional
    public void logout(LogoutRequest request) throws ParseException {
        refreshTokenRepository.deleteByToken(request.getRefreshToken());
    }

    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) throws JOSEException, ParseException {
        // Kiem tra token co hieu luc khong
        JWSVerifier verifier = new MACVerifier(REFRESH_TOKEN_KEY.getBytes());
        SignedJWT decodeRefreshToken = SignedJWT.parse(request.getRefreshToken());
        Date expiryTime = decodeRefreshToken.getJWTClaimsSet().getExpirationTime();
        var verified = decodeRefreshToken.verify(verifier);
        if (!(verified && expiryTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Kiểm tra có token trong db khong
        var refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken()).orElseThrow(() -> new AppException(ErrorCode.REFRESH_TOKEN_NOT_EXIST));

        // Sign access token và refresh token mới đồng thời xóa refresh token cũ trong db
        var accessToken = generateAccessToken(refreshToken.getAccount());
        var newRefreshToken = generateRefreshToken(refreshToken.getAccount());
        refreshTokenRepository.save(new RefreshToken(null, newRefreshToken, refreshToken.getAccount(), decodeRefreshToken.getJWTClaimsSet().getIssueTime(), decodeRefreshToken.getJWTClaimsSet().getExpirationTime()));
        refreshTokenRepository.deleteByToken(request.getRefreshToken());
        return RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    private String generateAccessToken(Account account) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .claim("accountId", account.getId())
                .claim("scope", account.getRole()) // ở đây thằng Spring hiểu role là scope để map vào authorization header
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(30, ChronoUnit.MINUTES).toEpochMilli()
                ))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(ACCESS_TOKEN_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private String generateRefreshToken(Account account) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .claim("accountId", account.getId())
                .claim("scope", account.getRole()) // ở đây thằng Spring hiểu role là scope để map vào authorization header
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli()
                ))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(REFRESH_TOKEN_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    public AuthenticationResponse loginGoogle(String email, String name, String avatar) throws ParseException {
        Optional<Account> accountOptional = accountRepository.findByEmail(email);
        Account account;

        // Nếu khách chưa có tài khoản trong db thì mình sẽ tạo tạm thời cho khách
        if (accountOptional.isEmpty()) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            AccountCreationRequest request = new AccountCreationRequest(name, email, passwordEncoder.encode("123123"), "Customer", 1);
            account = accountMapper.toAccount(request);
            account.setAvatar(avatar);
            accountRepository.save(account);
        } else {
            account = accountOptional.get(); // Lấy tài khoản từ Optional
        }

        // Tạo access token và refresh token
        var accessToken = generateAccessToken(account);
        var refreshToken = generateRefreshToken(account);
        SignedJWT decodeRefreshToken = SignedJWT.parse(refreshToken);

        // Lưu refresh token vào cơ sở dữ liệu
        refreshTokenRepository.save(new RefreshToken(
                null,
                refreshToken,
                account,
                decodeRefreshToken.getJWTClaimsSet().getIssueTime(),
                decodeRefreshToken.getJWTClaimsSet().getExpirationTime()
        ));

        // Trả về response
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .account(accountMapper.toAccountResponseClient(account))
                .build();
    }

    public void forgotPassword(ForgotPasswordRequest request) throws MessagingException {
        var account = accountRepository.findByEmail(request.getEmail()).orElseThrow(()
                -> new AppException(ErrorCode.EMAIL_NOT_EXIST));
        sendEmailService.sendEmail(account.getEmail(), "Mật khẩu xác nhận là: <b>123123</b>", "Xác nhận mật khẩu");
    }

}
