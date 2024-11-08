package com.swp.PodBookingSystem.controller;

import com.nimbusds.jose.JOSEException;
import com.swp.PodBookingSystem.dto.request.Authentication.*;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.request.IntrospectRequest;
import com.swp.PodBookingSystem.dto.respone.AuthenticationResponse;
import com.swp.PodBookingSystem.dto.respone.IntrospectResponse;

import com.swp.PodBookingSystem.dto.respone.RefreshTokenResponse;
import com.swp.PodBookingSystem.service.AuthenticationService;
import com.swp.PodBookingSystem.service.SendEmailService;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationController {
    AuthenticationService authenticationService;
    SendEmailService sendEmailService;
    @NonFinal
    @Value("${google.success}")
    protected String urlSuccess;

    @NonFinal
    @Value("${google.failure}")
    protected String urlFailure;

    @GetMapping("/login/google")
    public RedirectView loginGoogle(OAuth2AuthenticationToken token) throws ParseException, UnsupportedEncodingException {
        try {
            var result = authenticationService.loginGoogle(token.getPrincipal().getAttribute("email"),
                    token.getPrincipal().getAttribute("name"),
                    token.getPrincipal().getAttribute("picture"));
            return new RedirectView(urlSuccess + result.getAccessToken()
                    + "&refreshToken=" + result.getRefreshToken() + "&status=" + 200);
        } catch (Exception e) {
            String message = URLEncoder.encode("Tài khoản đã bị cấm", "UTF-8");
            return new RedirectView(urlFailure + message + "&status=" + 500);
        }
    }

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) throws ParseException {
        var result = authenticationService.login(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Đăng nhập thành công")
                .data(result)
                .build();
    }

    @PostMapping("/register")
    ApiResponse<AuthenticationResponse> register(@RequestBody RegisterRequest request) throws ParseException {
        var result = authenticationService.register(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Đăng kí thành công")
                .data(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.decodeToken(request);
        return ApiResponse.<IntrospectResponse>builder()
                .data(result).build();
    }

    @PostMapping("/refresh-token")
    ApiResponse<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<RefreshTokenResponse>builder()
                .message("Refresh token thành công")
                .data(result).build();
    }

    @PostMapping("/logout")
    ApiResponse logout(@RequestBody LogoutRequest request)
            throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.builder()
                .message("Đăng xuất thành công")
                .build();
    }

    @PostMapping("/forgot-password")
    ApiResponse forgotPassword(@RequestBody ForgotPasswordRequest request) throws MessagingException {
        authenticationService.forgotPassword(request);
        return ApiResponse.builder()
                .message("Gửi mail xác nhận thành công")
                .build();
    }

}
