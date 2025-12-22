package com.smartusers.logitrackapi.security;

import com.smartusers.logitrackapi.entity.RefreshToken;
import com.smartusers.logitrackapi.entity.User;
import com.smartusers.logitrackapi.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final long REFRESH_TOKEN_DURATION = 7 * 24 * 60 * 60 * 1000L;

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(REFRESH_TOKEN_DURATION))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public boolean isValid(RefreshToken refreshToken) {
        return refreshToken.getExpiryDate().isAfter(Instant.now());
    }

    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh Token not found"));
    }
}
