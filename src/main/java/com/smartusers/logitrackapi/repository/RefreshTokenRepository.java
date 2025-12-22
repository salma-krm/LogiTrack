package com.smartusers.logitrackapi.repository;

import com.smartusers.logitrackapi.entity.RefreshToken;
import com.smartusers.logitrackapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
