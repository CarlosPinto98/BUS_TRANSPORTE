package com.unimag.mappers;

import com.unimag.DTO.TripDTO.*;
import com.unimag.entities.Bus;
import com.unimag.entities.Route;
import com.unimag.entities.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TripMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", source = "date")
    @Mapping(target = "departureAt", source = "departureAt")
    @Mapping(target = "arrivalEta", source = "arrivalEta")
    @Mapping(target = "route", source = "routeId", qualifiedByName = "mapRoute")
    @Mapping(target = "bus", source = "busId", qualifiedByName = "mapBus")

    @Mapping(target = "statusTrip", expression = "java(com.unimag.entities.Enums.StatusTrip.SCHEDULED)")
    Trip toEntity(TripCreateRequest dto);


    @Mapping(target = "departureAt", source = "departureAt")
    @Mapping(target = "arrivalEta", source = "arrivalEta")
    @Mapping(target = "bus", source = "busId", qualifiedByName = "mapBus")

    @Mapping(target = "statusTrip", source = "statusTrip")
    void updateEntity(TripUpdateRequest dto, @MappingTarget Trip entity);

    @Mapping(target = "routeId", source = "route.id")
    @Mapping(target = "routeName", source = "route.name")
    @Mapping(target = "origin", source = "route.origin")
    @Mapping(target = "destination", source = "route.destination")
    @Mapping(target = "busId", source = "bus.id")
    @Mapping(target = "busPlate", source = "bus.plate")
    @Mapping(target = "capacity", source = "bus.capacity")

    @Mapping(target = "statusTrip", source = "statusTrip")
    TripResponse toResponse(Trip entity);

    @Named("mapRoute")
    default Route mapRoute(Long id) {
        if (id == null) return null;
        Route r = new Route();
        r.setId(id);
        return r;
    }

    @Named("mapBus")
    default Bus mapBus(Long id) {
        if (id == null) return null;
        Bus b = new Bus();
        b.setId(id);
        return b;
    }
}
