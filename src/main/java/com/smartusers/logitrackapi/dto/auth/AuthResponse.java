package com.smartusers.logitrackapi.dto.auth;
import com.smartusers.logitrackapi.dto.user.UserResponse;
import com.smartusers.logitrackapi.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
