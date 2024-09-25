package com.swp.PodBookingSystem.controller;

import com.nimbusds.jose.JOSEException;
import com.swp.PodBookingSystem.dto.request.Authentication.LogoutRequest;
import com.swp.PodBookingSystem.dto.request.Authentication.RefreshTokenRequest;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.request.Authentication.AuthenticationRequest;
import com.swp.PodBookingSystem.dto.request.IntrospectRequest;
import com.swp.PodBookingSystem.dto.respone.AuthenticationResponse;
import com.swp.PodBookingSystem.dto.respone.IntrospectResponse;

import com.swp.PodBookingSystem.dto.respone.RefreshTokenResponse;
import com.swp.PodBookingSystem.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationController {
    AuthenticationService authenticationService;

    @GetMapping("/login/google")
    public RedirectView loginGoogle(OAuth2AuthenticationToken token) throws ParseException {
        var result = authenticationService.loginGoogle(token.getPrincipal().getAttribute("email"),
                token.getPrincipal().getAttribute("name"),
                token.getPrincipal().getAttribute("picture"));
        return new RedirectView("http://localhost:3000/login/oauth?accessToken=" + result.getAccessToken()
                + "&refreshToken=" + result.getRefreshToken() + "&status=" + 200);
    }

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) throws ParseException {
        var result = authenticationService.login(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Login successfully")
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
                .message("Refresh token successfully")
                .data(result).build();
    }

    @PostMapping("/logout")
    ApiResponse logout(@RequestBody LogoutRequest request)
            throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.builder()
                .message("Logout successfully")
                .build();
    }

}
