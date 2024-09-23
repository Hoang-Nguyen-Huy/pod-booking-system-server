package com.swp.PodBookingSystem.controller;

import com.nimbusds.jose.JOSEException;
import com.swp.PodBookingSystem.dto.request.Authentication.RefreshTokenRequest;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.request.Authentication.AuthenticationRequest;
import com.swp.PodBookingSystem.dto.request.IntrospectRequest;
import com.swp.PodBookingSystem.dto.respone.AuthenticationResponse;
import com.swp.PodBookingSystem.dto.respone.IntrospectResponse;

import com.swp.PodBookingSystem.dto.respone.RefreshTokenResponse;
import com.swp.PodBookingSystem.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) throws ParseException {
        var result = authenticationService.login(request);
        return ApiResponse.<AuthenticationResponse>builder().data(result).build();
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
                .data(result).build();
    }

}
