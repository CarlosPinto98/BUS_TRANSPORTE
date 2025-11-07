package com.unimag.mappers;

import com.unimag.DTO.BusDTO.*;
import com.unimag.entities.Bus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BusMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "plate", source = "plate")
    @Mapping(target = "capacity", source = "capacity")
    @Mapping(target = "amenities", source = "amenities")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "seats", ignore = true)
    Bus toEntity(BusCreateRequest dto);

    @Mapping(target = "capacity", source = "capacity")
    @Mapping(target = "amenities", source = "amenities")
    @Mapping(target = "status", source = "status")
    void updateEntity(BusUpdateRequest dto, @MappingTarget Bus bus);

    @Mapping(target = "status", source = "status")
    BusResponse toResponse(Bus entity);

    default BusWithSeatsResponse toResponseWithSeats(Bus bus, Integer availableSeats) {
        if (bus == null) return null;

        return new BusWithSeatsResponse(
                bus.getId(),
                bus.getPlate(),
                bus.getCapacity(),
                bus.getAmenities(),
                bus.getStatusBus().name(),
                bus.getSeats() != null ? bus.getSeats().size() : 0,
                availableSeats
        );
    }
}
