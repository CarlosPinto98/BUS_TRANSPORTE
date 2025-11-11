package com.unimag.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.DTO.BusDTO.*;
import com.unimag.entities.Bus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface BusMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "plate", source = "plate")
    @Mapping(target = "capacity", source = "capacity")
    @Mapping(target = "amenities", source = "amenities")
    @Mapping(target = "statusBus", source = "statusBus")
    @Mapping(target = "seats", ignore = true)
    Bus toEntity(busCreateRequest dto);

    @Mapping(target = "capacity", source = "capacity")
    @Mapping(target = "amenities", source = "amenities")
    @Mapping(target = "statusBus", source = "statusBus")
    void updateEntity(busUpdateRequest dto, @MappingTarget Bus bus);

//    @Mapping(target = "statusBus", source = "statusBus")
//    busResponse toResponse(Bus entity);

//    default busWithSeatsResponse toResponseWithSeats(Bus bus, Integer availableSeats) {
//        if (bus == null) return null;
//
//        return new busWithSeatsResponse(
//                bus.getId(),
//                bus.getPlate(),
//                bus.getCapacity(),
//                bus.getAmenities(),
//                bus.getStatusBus().name(),
//                bus.getSeats() != null ? bus.getSeats().size() : 0,
//                availableSeats
//        );
//    }

    default busResponse toResponse(Bus entity) {
        if (entity == null) return null;

        String amenitiesJson = "{}";
        if (entity.getAmenities() != null && !entity.getAmenities().isEmpty()) {
            try {
                amenitiesJson = new com.fasterxml.jackson.databind.ObjectMapper()
                        .writeValueAsString(entity.getAmenities());
            } catch (Exception e) {
                amenitiesJson = "{}";
            }
        }

        return new busResponse(
                entity.getId(),
                entity.getPlate(),
                entity.getCapacity(),
                amenitiesJson,
                entity.getStatusBus() != null ? entity.getStatusBus().name() : null
        );
    }

    default busWithSeatsResponse toResponseWithSeats(Bus bus, Integer availableSeats) {
        if (bus == null) return null;

        return new busWithSeatsResponse(
                bus.getId(),
                bus.getPlate(),
                bus.getCapacity(),
                bus.getAmenities(),
                bus.getStatusBus().name(),
                bus.getSeats() != null ? bus.getSeats().size() : 0,
                availableSeats
        );
    }

    @Named("mapToJson")
    default String mapToJson(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
