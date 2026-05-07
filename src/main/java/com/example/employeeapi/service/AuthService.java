package com.example.employeeapi.service;

import com.example.employeeapi.dto.Auth;
import com.example.employeeapi.entity.RefreshToken;
import com.example.employeeapi.entity.User;
import com.example.employeeapi.entity.User.Role;
import com.example.employeeapi.exception.DuplicateResourceException;
import com.example.employeeapi.exception.ResourceNotFoundException;
import com.example.employeeapi.repository.UserRepository;
import com.example.employeeapi.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    // ── Register ──────────────────────────────────────────────────────────────

    @Transactional
    public Auth.UserInfo register(Auth.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        Set<Role> roles = (request.getRoles() == null || request.getRoles().isEmpty())
                ? Set.of(Role.ROLE_USER)
                : request.getRoles();

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(roles)
                .enabled(true)
                .build();

        User saved = userRepository.save(user);
        log.info("Registered new user: {}", saved.getUsername());
        return toUserInfo(saved);
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Transactional
    public Auth.JwtResponse login(Auth.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtUtils.generateAccessToken(userDetails);

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", userDetails.getUsername()));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        log.info("User logged in: {}", user.getUsername());

        return Auth.JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtUtils.getExpirationFromToken(accessToken).getTime())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }

    // ── Refresh Token ─────────────────────────────────────────────────────────

    @Transactional
    public Auth.JwtResponse refreshToken(Auth.RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(r -> new org.springframework.security.core.authority.SimpleGrantedAuthority(r.name()))
                        .collect(Collectors.toList()))
                .build();

        String newAccessToken = jwtUtils.generateAccessToken(userDetails);
        // Rotate: issue a new refresh token
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return Auth.JwtResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtUtils.getExpirationFromToken(newAccessToken).getTime())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    @Transactional
    public void logout(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            refreshTokenService.deleteByUserId(user.getId());
            log.info("User logged out: {}", username);
        });
    }

    // ── Me (current user info) ────────────────────────────────────────────────

    public Auth.UserInfo getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return toUserInfo(user);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Auth.UserInfo toUserInfo(User user) {
        return Auth.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles().stream().map(Role::name).collect(Collectors.toSet()))
                .build();
    }
}
