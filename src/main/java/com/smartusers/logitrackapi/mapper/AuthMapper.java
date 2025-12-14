package com.smartusers.logitrackapi.mapper;

import com.smartusers.logitrackapi.dto.auth.AuthResponse;
import com.smartusers.logitrackapi.dto.auth.RegisterRequest;
import com.smartusers.logitrackapi.entity.User;
import com.smartusers.logitrackapi.service.impl.SessionManager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;


@Mapper(componentModel = "spring")
public abstract class AuthMapper {


    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "role", source = "role")
    public abstract AuthResponse toAuthResponse(User user);

    // DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "password", source = "password")  
    public abstract User toEntity(RegisterRequest request);
}