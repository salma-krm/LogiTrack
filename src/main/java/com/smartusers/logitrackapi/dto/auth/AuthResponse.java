package com.smartusers.logitrackapi.dto.auth;

import com.smartusers.logitrackapi.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private Long id;
    private String refreshToken;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
