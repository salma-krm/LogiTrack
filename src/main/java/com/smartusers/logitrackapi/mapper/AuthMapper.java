package com.smartusers.logitrackapi.mapper;

import com.smartusers.logitrackapi.dto.auth.AuthResponse;
import com.smartusers.logitrackapi.entity.User;
import com.smartusers.logitrackapi.service.impl.SessionManager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class AuthMapper {

    @Autowired
    protected SessionManager sessionManager;

    @Mapping(target = "user", expression = "java(userMapper.toUserResponse(user))")
    @Mapping(target = "sessionId", expression = "java(sessionManager.createSession(user))")
    @Mapping(target = "message", constant = "Operation successful")
    public abstract AuthResponse toAuthResponse(User user);

    @Autowired
    protected UserMapper userMapper;
}
