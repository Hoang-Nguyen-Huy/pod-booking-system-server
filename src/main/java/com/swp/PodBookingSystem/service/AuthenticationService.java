package com.swp.PodBookingSystem.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.swp.PodBookingSystem.dto.request.Authentication.AuthenticationRequest;
import com.swp.PodBookingSystem.dto.request.Authentication.RefreshTokenRequest;
import com.swp.PodBookingSystem.dto.request.IntrospectRequest;
import com.swp.PodBookingSystem.dto.respone.AuthenticationResponse;
import com.swp.PodBookingSystem.dto.respone.IntrospectResponse;
import com.swp.PodBookingSystem.dto.respone.RefreshTokenResponse;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.entity.RefreshToken;
import com.swp.PodBookingSystem.exception.AppException;
import com.swp.PodBookingSystem.exception.ErrorCode;
import com.swp.PodBookingSystem.repository.AccountRepository;
import com.swp.PodBookingSystem.repository.RefreshTokenRepository;
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

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    AccountRepository accountRepository;
    RefreshTokenRepository refreshTokenRepository;
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
        var account = accountRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(),
                account.getPassword());
        if (!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        var accessToken = generateAccessToken(account);
        var refreshToken = generateRefreshToken(account);
        SignedJWT decodeRefreshToken = SignedJWT.parse(refreshToken);
        refreshTokenRepository.save(new RefreshToken(null, refreshToken, account, decodeRefreshToken.getJWTClaimsSet().getIssueTime(), decodeRefreshToken.getJWTClaimsSet().getExpirationTime()));

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) throws JOSEException, ParseException {
        // Kiem tra token co hieu luc khong
        JWSVerifier verifier = new MACVerifier(REFRESH_TOKEN_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(request.getRefreshToken());
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);
        if (!(verified && expiryTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Kiểm tra có token trong db khong
        var refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken()).orElseThrow(() -> new AppException(ErrorCode.REFRESH_TOKEN_NOT_EXIST));

        // Sign access token và refresh token mới đồng thời xóa refresh token cũ trong db
        var accessToken = generateAccessToken(refreshToken.getAccount());
        var newRefreshToken = generateRefreshToken(refreshToken.getAccount());
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
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
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

}
