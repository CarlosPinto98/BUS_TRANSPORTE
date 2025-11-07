package com.unimag.mappers;

import com.unimag.DTO.SeatDTO.*;
import com.unimag.entities.Bus;
import com.unimag.entities.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "number", source = "number")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "bus", source = "busId", qualifiedByName = "mapBus")
    Seat toEntity(SeatCreateRequest dto);

    @Mapping(target = "type", source = "type")
    void updateEntity(SeatUpdateRequest dto, @MappingTarget Seat seat);


    @Mapping(target = "busId", source = "bus.id")
    @Mapping(target = "busPlate", source = "bus.plate")
    @Mapping(target = "busCapacity", source = "bus.capacity")
    @Mapping(target = "type", source = "type")
    SeatResponse toResponse(Seat entity);
    @Named("mapBus")
    default Bus mapBus(Long id) {
        if (id == null) return null;
        Bus b = new Bus();
        b.setId(id);
        return b;
    }
}
