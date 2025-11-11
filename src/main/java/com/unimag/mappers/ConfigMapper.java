package com.unimag.mappers;

import com.unimag.DTO.ConfigDTO.*;
import com.unimag.entities.Config;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ConfigMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "key", source = "key")
    @Mapping(target = "value", source = "value")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "updatedAt", ignore = true)
    Config toEntity(configCreateRequest dto);


    @Mapping(target = "value", source = "value")
    @Mapping(target = "description", source = "description")
    void updateEntity(configUpdateRequest dto, @MappingTarget Config config);

    configResponse toResponse(Config entity);
}
