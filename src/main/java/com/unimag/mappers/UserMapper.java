package com.unimag.mappers;

import com.unimag.DTO.UserDTO.*;
import com.unimag.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "status", expression = "java(UserStatus.ACTIVE)")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(UserCreateRequest dto);

    @Mapping(target = "username", source = "username")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "status", source = "status")
    void updateEntity(UserUpdateRequest dto, @MappingTarget User user);

    @Mapping(target = "role", source = "role")
    @Mapping(target = "status", source = "status")
    UserResponse toResponse(User entity);
}
