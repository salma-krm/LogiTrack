//package com.smartusers.logitrackapi.service.impl;
//
//import com.smartusers.logitrackapi.Exception.BusinessException;
//import com.smartusers.logitrackapi.Exception.DuplicateResourceException;
//import com.smartusers.logitrackapi.dto.auth.AuthResponse;
//import com.smartusers.logitrackapi.dto.auth.LoginRequest;
//import com.smartusers.logitrackapi.dto.auth.RegisterRequest;
//import com.smartusers.logitrackapi.entity.User;
//import com.smartusers.logitrackapi.enums.Role;
//import com.smartusers.logitrackapi.mapper.AuthMapper;
//import com.smartusers.logitrackapi.mapper.UserMapper;
//import com.smartusers.logitrackapi.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class AuthServiceImplTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private UserMapper userMapper;
//
//    @Mock
//    private AuthMapper authMapper;
//
//    @Mock
//    private SessionManager sessionManager;
//
//    @InjectMocks
//    private AuthServiceImpl authService;
//
//    private RegisterRequest registerRequest;
//    private LoginRequest loginRequest;
//    private User user;
//    private AuthResponse authResponse;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        registerRequest = new RegisterRequest();
//        registerRequest.setFirstName("John");
//        registerRequest.setLastName("Doe");
//        registerRequest.setEmail("john@example.com");
//        registerRequest.setPassword("password123");
//        registerRequest.setRole(Role.MANAGER);
//
//        loginRequest = new LoginRequest();
//        loginRequest.setEmail("john@example.com");
//        loginRequest.setPassword("password123");
//
//        user = new User();
//        user.setId(1L);
//        user.setEmail("john@example.com");
//        user.setRole(Role.MANAGER);
//        user.setIsActive(true);
//
//        authResponse = AuthResponse.builder()
//                .user(userMapper.toUserResponse(user))
//                .sessionId("session123")
//                .build();
//    }
//
//    // ================== REGISTER ==================
//    @Test
//    void testRegister_Success() {
//        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
//        when(userMapper.toEntity(registerRequest)).thenReturn(user);
//        when(userRepository.save(user)).thenReturn(user);
//        when(authMapper.toAuthResponse(user)).thenReturn(authResponse);
//
//        AuthResponse response = authService.register(registerRequest);
//
//        assertNotNull(response);
//        assertEquals("Success", response.getMessage());
//        verify(userRepository, times(1)).save(user);
//    }
//
//    @Test
//    void testRegister_EmailAlreadyExists() {
//        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);
//
//        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class,
//                () -> authService.register(registerRequest));
//        assertEquals("This email is already in use", ex.getMessage());
//    }
//
//    // ================== LOGIN ==================
//    @Test
//    void testLogin_Success() {
//        // Simuler le mot de passe hashé correspondant
//        user.setPassword(authService.encodePassword("password123"));
//
//        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
//        when(authMapper.toAuthResponse(user)).thenReturn(authResponse);
//
//        AuthResponse response = authService.login(loginRequest);
//
//        assertNotNull(response);
//        assertEquals("Success", response.getMessage());
//    }
//
//    @Test
//    void testLogin_InvalidPassword() {
//        user.setPassword(authService.encodePassword("wrongpassword"));
//        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
//
//        BusinessException ex = assertThrows(BusinessException.class,
//                () -> authService.login(loginRequest));
//        assertEquals("Invalid email or password", ex.getMessage());
//    }
//
//    @Test
//    void testLogin_UserNotActive() {
//        user.setIsActive(false);
//        user.setPassword(authService.encodePassword("password123"));
//        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
//
//        BusinessException ex = assertThrows(BusinessException.class,
//                () -> authService.login(loginRequest));
//        assertEquals("Account is not active", ex.getMessage());
//    }
//
//    // ================== LOGOUT ==================
//    @Test
//    void testLogout() {
//        doNothing().when(sessionManager).invalidateSession("session123");
//        authService.logout("session123");
//        verify(sessionManager, times(1)).invalidateSession("session123");
//    }
//}
