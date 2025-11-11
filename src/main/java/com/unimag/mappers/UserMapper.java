package com.unimag.mappers;

import com.unimag.DTO.UserDTO.*;
import com.unimag.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "statusUser", expression = "java(com.unimag.entities.Enums.StatusUser.ACTIVE)")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(userCreateRequest dto);

    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "statusUser", source = "statusUser")
    void updateEntity(userUpdateRequest dto, @MappingTarget User user);

    @Mapping(target = "role", source = "role")
    @Mapping(target = "statusUser", source = "statusUser")
    userResponse toResponse(User entity);

}
