package com.example.employeeapi.dto;

import com.example.employeeapi.entity.User.Role;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

// ── Register Request ──────────────────────────────────────────────────────────

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    public String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    public String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    public String password;

    @Size(min = 2, max = 50)
    public String firstName;

    @Size(min = 2, max = 50)
    public String lastName;

    // Roles are optional at registration; defaults to ROLE_USER if omitted
    public Set<Role> roles;
}

// ── Login Request ─────────────────────────────────────────────────────────────

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class LoginRequest {

    @NotBlank(message = "Username is required")
    public String username;

    @NotBlank(message = "Password is required")
    public String password;
}

// ── Token Response ────────────────────────────────────────────────────────────

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class JwtResponse {
    public String accessToken;
    public String refreshToken;
    public String tokenType = "Bearer";
    public long expiresIn;
    public String username;
    public String email;
    public Set<String> roles;
}

// ── Refresh Token Request ─────────────────────────────────────────────────────

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
class RefreshTokenRequest {
    @NotBlank(message = "Refresh token is required")
    public String refreshToken;
}
