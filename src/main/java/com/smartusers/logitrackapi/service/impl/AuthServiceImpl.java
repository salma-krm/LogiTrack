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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final SessionManager sessionManager;

    // ================== REGISTER ==================
    @Transactional
    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("This email is already in use");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User savedUser = userRepository.save(user);

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

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid email or password");
        }

        // Optionally create session
        return authMapper.toAuthResponse(user);
    }

    // ================== LOGOUT ==================
    @Override
    public void logout(String sessionId) {
        sessionManager.invalidateSession(sessionId);
    }
}
