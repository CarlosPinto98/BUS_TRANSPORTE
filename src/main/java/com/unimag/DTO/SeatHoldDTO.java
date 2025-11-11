package com.unimag.DTO;

import com.unimag.entities.Enums.StatusSeatHold;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SeatHoldDTO {

    public record seatHoldCreateRequest(
            @NotNull(message = "tripId is required")
            Long tripId,
            @NotBlank(message = "seatNumber is required")
            @Size(max = 10, message = "seatNumber must not exceed 10 characters")
            String seatNumber,
            @NotNull(message = "fromStopId is required")
            Long fromStopId,
            @NotNull(message = "toStopId is required")
            Long toStopId) implements Serializable {}

    public record seatHoldUpdateRequest(
            @NotNull(message = "status is required")
            StatusSeatHold statusSeatHold) implements Serializable {}

    public record seatHoldResponse(
            Long id,
            String seatNumber,
            LocalDateTime expiresAt,
            String statusSeatHold,
            LocalDateTime createdAt,
            Long tripId,
            Long userId,
            String tripDate,
            String tripTime,
            String routeName,
            Integer minutesLeft) implements Serializable {}
}
