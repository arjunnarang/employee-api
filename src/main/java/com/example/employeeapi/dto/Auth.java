package com.example.employeeapi.dto;

import com.example.employeeapi.entity.User.Role;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

public class Auth {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RegisterRequest {
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50)
        private String username;

        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email address")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 100)
        private String password;

        @Size(min = 2, max = 50)
        private String firstName;

        @Size(min = 2, max = 50)
        private String lastName;

        private Set<Role> roles;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class LoginRequest {
        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class JwtResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private long expiresIn;
        private String username;
        private String email;
        private Set<String> roles;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RefreshTokenRequest {
        @NotBlank(message = "Refresh token is required")
        private String refreshToken;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private Set<String> roles;
    }
}
