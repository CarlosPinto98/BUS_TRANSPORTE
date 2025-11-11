package com.unimag.mappers;

import com.unimag.DTO.RouteDTO.*;
import com.unimag.entities.Route;
import com.unimag.entities.Stop;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RouteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stops", ignore = true)
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "origin", source = "origin")
    @Mapping(target = "destination", source = "destination")
    @Mapping(target = "distanceKm", source = "distanceKm")
    @Mapping(target = "durationMin", source = "durationMin")
    Route toEntity(routeCreateRequest dto);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "distanceKm", source = "distanceKm")
    @Mapping(target = "durationMin", source = "durationMin")
    void updateEntity(routeUpdateRequest dto, @MappingTarget Route route);

    @Mapping(target = "stops", source = "stops", qualifiedByName = "mapStopsToSummary")
    routeResponse toResponse(Route entity);

    @Named("mapStopsToSummary")
    default List<stopSummary> mapStopsToSummary(List<Stop> stops) {
        if (stops == null) return null;
        return stops.stream()
                .map(stop -> new stopSummary(
                        stop.getId(),
                        stop.getName(),
                        stop.getOrder(),
                        stop.getLat(),
                        stop.getLng()
                ))
                .toList();
    }
}
