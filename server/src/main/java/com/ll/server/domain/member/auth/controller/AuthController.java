package com.ll.server.domain.member.auth.controller;

import com.ll.server.domain.member.auth.dto.AuthResponse;
import com.ll.server.domain.member.auth.dto.LoginRequest;
import com.ll.server.domain.member.auth.dto.SignupRequest;
import com.ll.server.domain.member.auth.dto.SocialLoginRequest;
import com.ll.server.domain.member.auth.service.AuthService;
import com.ll.server.global.response.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<Void> signup(
            @RequestBody @Valid SignupRequest signupRequest
    ) {
        authService.signup(signupRequest);
        return new ApiResponse<>();
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login (
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        AuthResponse authResponse = authService.login(loginRequest, response);
        return ApiResponse.of(authResponse);
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh (
            @RequestBody String refreshToken,
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        AuthResponse authResponse = authService.refresh(refreshToken, response, request);
        return ApiResponse.of(authResponse);
    }

    @PostMapping("/social/login")
    public ApiResponse<AuthResponse> socialLogin (
            @Valid @RequestBody SocialLoginRequest socialLoginRequest,
            HttpServletResponse response
    ) {
        AuthResponse authResponse = authService.socialLogin(socialLoginRequest, response);
        return ApiResponse.of(authResponse);
    }
}
