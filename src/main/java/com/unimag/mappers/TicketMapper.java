package com.unimag.mappers;

import com.unimag.DTO.TicketDTO.*;
import com.unimag.entities.Stop;
import com.unimag.entities.Ticket;
import com.unimag.entities.Trip;
import com.unimag.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trip", source = "tripId", qualifiedByName = "mapTrip")
    @Mapping(target = "passenger", source = "passengerId", qualifiedByName = "mapUser")
    @Mapping(target = "fromStop", source = "fromStopId", qualifiedByName = "mapStop")
    @Mapping(target = "toStop", source = "toStopId", qualifiedByName = "mapStop")
    @Mapping(target = "status", expression = "java(com.example.busconnect.domine.entities.enums.TicketStatus.SOLD)")
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "qrCode", ignore = true)
    Ticket toEntity(TicketCreateRequest dto);

    @Mapping(target = "status", source = "status")
    void updateEntity(TicketUpdateRequest dto, @MappingTarget Ticket entity);

    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "tripDate", source = "trip.date", qualifiedByName = "formatDate")
    @Mapping(target = "tripTime", source = "trip.departureAt", qualifiedByName = "formatTime")
    @Mapping(target = "passengerId", source = "passenger.id")
    @Mapping(target = "passengerName", source = "passenger.username")
    @Mapping(target = "fromStopId", source = "fromStop.id")
    @Mapping(target = "fromStopName", source = "fromStop.name")
    @Mapping(target = "toStopId", source = "toStop.id")
    @Mapping(target = "toStopName", source = "toStop.name")
    TicketResponse toResponse(Ticket entity);

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

    @Named("mapStop")
    default Stop mapStop(Long id) {
        if (id == null) return null;
        Stop s = new Stop();
        s.setId(id);
        return s;
    }

    @Named("formatDate")
    default String formatDate(java.time.LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
    }

    @Named("formatTime")
    default String formatTime(java.time.LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("HH:mm")) : null;
    }
}
