package com.smartusers.logitrackapi.controllers;
import com.smartusers.logitrackapi.dto.auth.AuthResponse;
import com.smartusers.logitrackapi.dto.auth.LoginRequest;

import com.smartusers.logitrackapi.dto.auth.RegisterRequest;
import com.smartusers.logitrackapi.dto.auth.TokenRefreshRequest;
import com.smartusers.logitrackapi.security.JwtService;
import com.smartusers.logitrackapi.security.RefreshTokenService;
import com.smartusers.logitrackapi.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request) {
        log.info("Register request received for email={}", request.getEmail());
        AuthResponse resp = authService.register(request);
        log.debug("Register response for email={} id={}", resp.getEmail(), resp.getId());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {
        log.info("Login attempt for email={}", request.getEmail());
        AuthResponse resp = authService.login(request);
        log.debug("Login successful for email={} id={}", resp.getEmail(), resp.getId());
        return ResponseEntity.ok(resp);
    }

    // ✅ REFRESH TOKEN
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestBody TokenRefreshRequest request) {
        log.info("Refresh token request received");

        var refreshToken = refreshTokenService.findByToken(request.getRefreshToken());

        if (!refreshTokenService.isValid(refreshToken)) {
            log.warn("Invalid or expired refresh token: {}", request.getRefreshToken());
            throw new RuntimeException("Refresh token expired");
        }

        String newAccessToken =
                jwtService.generateToken(refreshToken.getUser().getEmail());

        AuthResponse response = AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .id(refreshToken.getUser().getId())
                .email(refreshToken.getUser().getEmail())
                .role(refreshToken.getUser().getRole())
                .build();

        log.debug("Refresh token rotated for userId={}", response.getId());
        return ResponseEntity.ok(response);
    }


    @PostMapping("/logout/{userId}")
    public ResponseEntity<String> logout(@PathVariable Long userId) {
        log.info("Logout request for userId={}", userId);

        refreshTokenService.deleteByUser(
                refreshTokenService.findByToken(
                        refreshTokenService.findByToken(userId.toString()).getToken()
                ).getUser()
        );

        return ResponseEntity.ok("Logged out successfully");
    }
}
