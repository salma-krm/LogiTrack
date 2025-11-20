package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.Exception.BusinessException;
import com.smartusers.logitrackapi.Exception.DuplicateResourceException;
import com.smartusers.logitrackapi.dto.auth.AuthResponse;
import com.smartusers.logitrackapi.dto.auth.LoginRequest;
import com.smartusers.logitrackapi.dto.auth.RegisterRequest;
import com.smartusers.logitrackapi.entity.User;
import com.smartusers.logitrackapi.mapper.AuthMapper;
import com.smartusers.logitrackapi.mapper.UserMapper;
import com.smartusers.logitrackapi.repository.UserRepository;
import com.smartusers.logitrackapi.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthMapper authMapper; // Mapper pour AuthResponse
    private final SessionManager sessionManager;

    // ================== REGISTER ==================
    @Transactional
    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("This email is already in use");
        }

        // Convertir DTO -> User
        User user = userMapper.toEntity(request);

        // Hasher le mot de passe
        user.setPassword(encodePassword(request.getPassword()));

        // Sauvegarder dans la base
        User savedUser = userRepository.save(user);

        // Retourner AuthResponse avec mapper
        return authMapper.toAuthResponse(savedUser);
    }

    // ================== LOGIN ==================
    @Transactional(readOnly = true)
    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid email or password"));

        if (!user.getIsActive()) {
            throw new BusinessException("Account is not active");
        }

        if (!checkPassword(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid email or password");
        }

        return authMapper.toAuthResponse(user);
    }

    // ================== LOGOUT ==================
    @Override
    public void logout(String sessionId) {
        sessionManager.invalidateSession(sessionId);
    }

    // ================== PASSWORD UTILS ==================
    public String encodePassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new BusinessException("Error encoding password");
        }
    }

    private boolean checkPassword(String rawPassword, String encodedPassword) {
        return encodePassword(rawPassword).equals(encodedPassword);
    }
}
