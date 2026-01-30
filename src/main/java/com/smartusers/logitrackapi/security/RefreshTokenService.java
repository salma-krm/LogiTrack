package com.smartusers.logitrackapi.security;

import com.smartusers.logitrackapi.entity.RefreshToken;
import com.smartusers.logitrackapi.entity.User;
import com.smartusers.logitrackapi.repository.RefreshTokenRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final EntityManager entityManager;

    private final long REFRESH_TOKEN_DURATION = 7 * 24 * 60 * 60 * 1000L;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Supprimer l'ancien refresh token s'il existe
        refreshTokenRepository.findByUser(user).ifPresent(oldToken -> {
            refreshTokenRepository.delete(oldToken);
            entityManager.flush();
        });


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

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
        entityManager.flush();
    }
}
