package com.smartusers.logitrackapi.service.interfaces;

import com.smartusers.logitrackapi.dto.auth.AuthResponse;
import com.smartusers.logitrackapi.dto.auth.LoginRequest;
import com.smartusers.logitrackapi.dto.auth.RegisterRequest;

public interface AuthService {

    public AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);


}
