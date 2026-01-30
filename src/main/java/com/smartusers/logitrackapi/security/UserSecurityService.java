package com.smartusers.logitrackapi.security;

import com.smartusers.logitrackapi.entity.User;
import com.smartusers.logitrackapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("userSecurityService")
@RequiredArgsConstructor
public class UserSecurityService {

    private final UserRepository userRepository;

    /**
     * Vérifie si l'utilisateur authentifié est le propriétaire du profil
     */
    public boolean isOwner(Long userId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(user -> user.getId().equals(userId))
                .orElse(false);
    }
}

