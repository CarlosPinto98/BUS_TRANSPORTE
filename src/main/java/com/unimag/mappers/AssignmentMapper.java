package com.unimag.mappers;

import com.unimag.DTO.*;
import com.unimag.entities.Assignment;
import com.unimag.entities.Trip;
import com.unimag.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trip", source = "tripId", qualifiedByName = "mapTrip")
    @Mapping(target = "driver", source = "driverId", qualifiedByName = "mapUser")
    @Mapping(target = "dispatcher", source = "dispatcherId", qualifiedByName = "mapUser")
    @Mapping(target = "checklistOk", constant = "false")
    @Mapping(target = "assignedAt", ignore = true)
    Assignment toEntity(AssignmentDTO.assignmentCreateRequest dto);


    @Mapping(target = "checklistOk", source = "checklistOk")
    void updateEntity(AssignmentDTO.assignmentUpdateRequest dto, @MappingTarget Assignment entity);

    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "tripInfo", source = "trip", qualifiedByName = "formatTripInfo")
    @Mapping(target = "statusTrip",source = "trip.statusTrip")
    @Mapping(target = "driverId", source = "driver.id")
    @Mapping(target = "driverName", source = "driver.name")
    @Mapping(target = "dispatcherId", source = "dispatcher.id")
    @Mapping(target = "dispatcherName", source = "dispatcher.name")
    AssignmentDTO.assignmentResponse toResponse(Assignment entity);

    @Named("mapTrip")
    default Trip mapTrip(Long id) {
        if (id == null) return null;
        Trip t = new Trip();
        t.setId(id);
        return t;
    }

    @Named("mapUser")
    default User mapUser(Long id) {
        if (id == null) return null;
        User u = new User();
        u.setId(id);
        return u;
    }

    @Named("formatTripInfo")
    default String formatTripInfo(Trip trip) {
        if (trip == null) return null;
        return trip.getRoute().getOrigin() + " → " +
                trip.getRoute().getDestination() + " - " +
                trip.getDate() + " " +
                trip.getDepartureAt().toLocalTime();
    }
}
