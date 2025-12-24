package com.smartusers.logitrackapi.controllers;
import com.smartusers.logitrackapi.dto.auth.AuthResponse;
import com.smartusers.logitrackapi.dto.auth.LoginRequest;

import com.smartusers.logitrackapi.dto.auth.RegisterRequest;
import com.smartusers.logitrackapi.dto.auth.TokenRefreshRequest;
import com.smartusers.logitrackapi.security.JwtService;
import com.smartusers.logitrackapi.security.RefreshTokenService;
import com.smartusers.logitrackapi.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    // ✅ REGISTER
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(authService.register(request));
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    // ✅ REFRESH TOKEN
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestBody TokenRefreshRequest request) {

        var refreshToken = refreshTokenService.findByToken(request.getRefreshToken());

        if (!refreshTokenService.isValid(refreshToken)) {
            throw new RuntimeException("Refresh token expired");
        }

        String newAccessToken =
                jwtService.generateToken(refreshToken.getUser().getEmail());

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .token(newAccessToken)
                        .refreshToken(refreshToken.getToken())
                        .id(refreshToken.getUser().getId())
                        .email(refreshToken.getUser().getEmail())
                        .role(refreshToken.getUser().getRole())
                        .build()
        );
    }

    // ✅ LOGOUT
    @PostMapping("/logout/{userId}")
    public ResponseEntity<String> logout(@PathVariable Long userId) {

        refreshTokenService.deleteByUser(
                refreshTokenService.findByToken(
                        refreshTokenService.findByToken(userId.toString()).getToken()
                ).getUser()
        );

        return ResponseEntity.ok("Logged out successfully");
    }
}
