package com.example.employeeapi.controller;

import com.example.employeeapi.dto.ApiResponse;
import com.example.employeeapi.dto.Auth;
import com.example.employeeapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login, token refresh, and logout")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user account")
    public ResponseEntity<ApiResponse<Auth.UserInfo>> register(
            @Valid @RequestBody Auth.RegisterRequest request) {

        Auth.UserInfo user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", user));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive JWT access + refresh tokens")
    public ResponseEntity<ApiResponse<Auth.JwtResponse>> login(
            @Valid @RequestBody Auth.LoginRequest request) {

        Auth.JwtResponse jwt = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", jwt));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Get a new access token using a refresh token")
    public ResponseEntity<ApiResponse<Auth.JwtResponse>> refreshToken(
            @Valid @RequestBody Auth.RefreshTokenRequest request) {

        Auth.JwtResponse jwt = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", jwt));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Logout — invalidates the refresh token")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal UserDetails userDetails) {

        authService.logout(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get currently authenticated user's info")
    public ResponseEntity<ApiResponse<Auth.UserInfo>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {

        Auth.UserInfo info = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("User info fetched", info));
    }
}
