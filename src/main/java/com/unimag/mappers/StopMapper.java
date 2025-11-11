package com.unimag.mappers;

import com.unimag.DTO.StopDTO.*;
import com.unimag.entities.Route;
import com.unimag.entities.Stop;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface StopMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "order", source = "order")
    @Mapping(target = "lat", source = "lat")
    @Mapping(target = "lng", source = "lng")
    @Mapping(target = "route", source = "routeId", qualifiedByName = "mapRoute")
    Stop toEntity(stopCreateRequest dto);


    @Mapping(target = "name", source = "name")
    @Mapping(target = "order", source = "order")
    void updateEntity(stopUpdateRequest dto, @MappingTarget Stop stop);


    @Mapping(target = "routeId", source = "route.id")
    @Mapping(target = "routeName", source = "route.name")
    @Mapping(target = "routeCode", source = "route.code")
    stopResponse toResponse(Stop entity);
    @Named("mapRoute")
    default Route mapRoute(Long id) {
        if (id == null) return null;
        Route r = new Route();
        r.setId(id);
        return r;
    }
}
