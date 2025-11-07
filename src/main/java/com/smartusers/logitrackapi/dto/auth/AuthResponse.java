package com.smartusers.logitrackapi.dto.auth;
import com.smartusers.logitrackapi.dto.user.UserResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String message;
    private UserResponse user;
    private String sessionId;
}
