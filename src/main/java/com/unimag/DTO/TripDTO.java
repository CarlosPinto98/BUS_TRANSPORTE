package com.unimag.DTO;

import com.unimag.entities.Enums.StatusTrip;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TripDTO {

    public record tripCreateRequest(
            @NotNull(message = "date is required")
            @FutureOrPresent(message = "date must be today or in the future")
            LocalDate date,

            @NotNull(message = "departureAt is required")
            LocalDateTime departureAt,

            @NotNull(message = "arrivalEta is required")
            LocalDateTime arrivalEta,

            @NotNull(message = "routeId is required")
            Long routeId,

            Long busId) implements Serializable {}

    public record tripUpdateRequest(
            @NotNull(message = "departureAt is required")
            LocalDateTime departureAt,
            @NotNull(message = "arrivalEta is required")
            LocalDateTime arrivalEta,
            Long busId,
            @NotNull(message = "status is required")
            StatusTrip statusTrip) implements Serializable {}

    public record tripResponse(
            Long id,
            LocalDate date,
            LocalDateTime departureAt,
            LocalDateTime arrivalEta,
            String statusTrip,
            Long routeId,
            String routeName,
            String origin,
            String destination,
            Long busId,
            String busPlate,
            Integer capacity) implements Serializable {}

    String mapStatusTrip(StatusTrip status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }
}
