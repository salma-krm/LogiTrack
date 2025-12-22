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
import com.smartusers.logitrackapi.security.CustomUserDetailsService;
import com.smartusers.logitrackapi.security.JwtService;
import com.smartusers.logitrackapi.security.RefreshTokenService;
import com.smartusers.logitrackapi.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Transactional
    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already in use");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }



    @Transactional
    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid email or password"));

        if (!user.getIsActive()) throw new BusinessException("Account not active");
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new BusinessException("Invalid email or password");

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        String accessToken = jwtService.generateToken(userDetails);


        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken.getToken())
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

}
