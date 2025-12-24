package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.Exception.BusinessException;
import com.smartusers.logitrackapi.Exception.DuplicateResourceException;
import com.smartusers.logitrackapi.dto.auth.AuthResponse;
import com.smartusers.logitrackapi.dto.auth.LoginRequest;
import com.smartusers.logitrackapi.dto.auth.RegisterRequest;
import com.smartusers.logitrackapi.entity.RefreshToken;
import com.smartusers.logitrackapi.entity.User;
import com.smartusers.logitrackapi.mapper.UserMapper;
import com.smartusers.logitrackapi.repository.UserRepository;
import com.smartusers.logitrackapi.security.JwtService;
import com.smartusers.logitrackapi.security.RefreshTokenService;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    // ✅ REGISTER
    @Transactional
    @Override
    public AuthResponse register(RegisterRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already in use");
        }

        User user = userMapper.toEntity(request);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
        user.setIsActive(true);

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());

        return buildResponse(user, token, null);
    }

    // ✅ LOGIN
    @Transactional
    @Override
    public AuthResponse login(LoginRequest request) {

        String email = request.getEmail().trim().toLowerCase();
        String password = request.getPassword().trim();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Invalid email or password"));

        if (!user.getIsActive()) {
            throw new BusinessException("Account not active");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("Invalid email or password");
        }

        String accessToken = jwtService.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return buildResponse(user, accessToken, refreshToken.getToken());
    }

    private AuthResponse buildResponse(User user, String token, String refreshToken) {
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
