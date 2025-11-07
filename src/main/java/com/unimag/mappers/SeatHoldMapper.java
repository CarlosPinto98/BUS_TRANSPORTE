package com.unimag.mappers;

import com.unimag.DTO.SeatHoldDTO.*;
import com.unimag.entities.SeatHold;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", uses = {TripMapper.class })
public interface SeatHoldMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trip", source = "tripId", qualifiedByName = "mapTrip")
    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUser")
    @Mapping(target = "expiresAt", ignore = true)
    @Mapping(target = "status", expression = "java(com.example.busconnect.domine.entities.enums.HoldStatus.HOLD)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "seatNumber", source = "seatNumber")
    SeatHold toEntity(SeatHoldCreateRequest dto);

    @Mapping(target = "status", source = "status")
    void updateEntity(SeatHoldUpdateRequest dto, @MappingTarget SeatHold entity);

    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "tripDate", source = "trip.date", qualifiedByName = "formatDate")
    @Mapping(target = "tripTime", source = "trip.departureAt", qualifiedByName = "formatTime")
    @Mapping(target = "routeName", source = "trip.route.name")
    @Mapping(target = "minutesLeft", source = "expiresAt", qualifiedByName = "calculateMinutesLeft")
    @Mapping(target = "status", source = "status")
    SeatHoldResponse toResponse(SeatHold entity);

    @Named("formatDate")
    default String formatDate(java.time.LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
    }

    @Named("formatTime")
    default String formatTime(java.time.LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("HH:mm")) : null;
    }

    @Named("calculateMinutesLeft")
    default Integer calculateMinutesLeft(LocalDateTime expiresAt) {
        if (expiresAt == null) return null;
        long minutes = Duration.between(LocalDateTime.now(), expiresAt).toMinutes();
        return minutes > 0 ? (int) minutes : 0;
    }
}
