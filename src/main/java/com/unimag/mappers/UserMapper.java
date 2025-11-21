package com.unimag.mappers;

import com.unimag.DTO.UserDTO.*;
import com.unimag.entities.User;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {


//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "createAt", expression = "java(java.time.LocalDateTime.now())")
//    @Mapping(target = "statusUser", expression = "java(com.unimag.entities.Enums.StatusUser.ACTIVE)")
//    @Mapping(target = "role", source = "role")
//    @Mapping(target = "email", source = "email")
//    @Mapping(target = "name", source = "name")
//    @Mapping(target = "passwordHash", ignore = true)}
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true) // Ignorar passwordHash en el DTO
    @Mapping(target = "seatHolds", ignore = true)    // Ignorar seatHolds (propiedad de relación)
    @Mapping(target = "dateOfBirth", ignore = true)  // Ignorar dateOfBirth si no está en el DTO

    @Mapping(target = "createAt", expression = "java(java.time.LocalDateTime.now())")
    // Se mantiene esta expresión para asignar el estado ACTIVE, la previa (@Mapping(target = "statusUser", ignore = true)) se elimina por ser duplicada.
    @Mapping(target = "statusUser", expression = "java(com.unimag.entities.Enums.StatusUser.ACTIVE)")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", source = "name")
    User toEntity(userCreateRequest dto);

//    @Mapping(target = "statusUser", ignore = true)
//    @Mapping(target = "email", source = "email")

//    @Mapping(target = "phone", source = "phone")
//    //@Mapping(target = "statusUser", source = "statusUser")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)

    @Mapping(target = "createAt", ignore = true)

    @Mapping(target = "seatHolds", ignore = true)
    @Mapping(target = "dateOfBirth", ignore = true)

    @Mapping(target = "statusUser", ignore = true) // Mantener el estado actual

    @Mapping(target = "email", source = "name")
    @Mapping(target = "phone", source = "phone")
    void updateEntity(@Valid userUpdateRequest dto, @MappingTarget User user);

    //@Mapping(target = "seatHolds", ignore = true)
    @Mapping(target = "role", source = "role")
    @Mapping(target = "statusUser", source = "statusUser")
    userResponse toResponse(User entity);

}
