package com.smartusers.logitrackapi.controllers;
import com.smartusers.logitrackapi.dto.auth.AuthResponse;
import com.smartusers.logitrackapi.dto.auth.LoginRequest;
import com.smartusers.logitrackapi.dto.auth.RegisterRequest;
import com.smartusers.logitrackapi.dto.auth.TokenRefreshRequest;
import com.smartusers.logitrackapi.entity.RefreshToken;
import com.smartusers.logitrackapi.entity.User;
import com.smartusers.logitrackapi.security.CustomUserDetailsService;
import com.smartusers.logitrackapi.security.JwtService;
import com.smartusers.logitrackapi.security.RefreshTokenService;
import com.smartusers.logitrackapi.service.impl.AuthServiceImpl;
import com.smartusers.logitrackapi.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService  refreshTokenService;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity
                .status(201)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity
                .ok(authService.login(request));
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());

        if (!refreshTokenService.isValid(refreshToken)) {
            throw new RuntimeException("Refresh token expired");
        }

        User user = refreshToken.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String newAccessToken = jwtService.generateToken(userDetails);

        refreshTokenService.deleteByUser(user);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build());
    }


}
