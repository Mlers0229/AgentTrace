package com.agenttrace.auth.web;

import com.agenttrace.auth.dto.AuthTokenResponse;
import com.agenttrace.auth.dto.LoginRequest;
import com.agenttrace.auth.dto.RegisterRequest;
import com.agenttrace.auth.dto.UserResponse;
import com.agenttrace.auth.security.CurrentUser;
import com.agenttrace.auth.service.AuthService;
import com.agenttrace.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me() {
        return ApiResponse.success(authService.currentUser(CurrentUser.require()));
    }
}

