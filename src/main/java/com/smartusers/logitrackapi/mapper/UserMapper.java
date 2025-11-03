package com.smartusers.logitrackapi.mapper;

import com.smartusers.logitrackapi.dto.auth.RegisterRequest;
import com.smartusers.logitrackapi.dto.user.UserResponse;
import com.smartusers.logitrackapi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", expression = "java(true)")
    User toEntity(RegisterRequest request);


    UserResponse toUserResponse(User user);
}
