package com.unimag.mappers;

import com.unimag.DTO.IncidentDTO.*;
import com.unimag.entities.Incident;
import com.unimag.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface IncidentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "entityId", source = "entityId")
    @Mapping(target = "note", source = "note")
    @Mapping(target = "reportedBy",  ignore = true, qualifiedByName = "mapUser")
    @Mapping(target = "createdAt", ignore = true)
    Incident toEntity(IncidentCreateRequest dto);

    @Mapping(target = "note", source = "note")
    void updateEntity(IncidentUpdateRequest dto, @MappingTarget Incident entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "entityType", source = "entityType", qualifiedByName = "enumToString")
    @Mapping(target = "entityId", source = "entityId")
    @Mapping(target = "type", source = "typeIncident", qualifiedByName = "enumToString")
    @Mapping(target = "note", source = "note")
    @Mapping(target = "reportedBy", source = "reportedBy.id")
    @Mapping(target = "reportedByName", source = "reportedBy.name")
    @Mapping(target = "createdAt", source = "createdAt")
    IncidentResponse toResponse(Incident entity);

    @Named("mapUser")
    default User mapUser(Long id) {
        if (id == null) return null;
        User u = new User();
        u.setId(id);
        return u;
    }

    @Named("enumToString")
    default String enumToString(Enum<?> value) {
        return value != null ? value.name() : null;
    }
}
