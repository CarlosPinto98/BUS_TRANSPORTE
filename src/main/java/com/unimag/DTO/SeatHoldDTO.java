package com.unimag.DTO;

import com.unimag.entities.Enums.StatusSeatHold;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SeatHoldDTO {

    public record SeatHoldCreateRequest(
            @NotNull(message = "tripId is required")
            Long tripId,
            @NotBlank(message = "seatNumber is required")
            @Size(max = 10, message = "seatNumber must not exceed 10 characters")
            String seatNumber,
            @NotNull(message = "userId is required")
            Long userId
    ) implements Serializable {}

    public record SeatHoldUpdateRequest(
            @NotNull(message = "status is required")
            StatusSeatHold statusSeatHold
    ) implements Serializable {}

    public record SeatHoldResponse(
            Long id,
            String seatNumber,
            LocalDateTime expiresAt,
            String status,
            LocalDateTime createAt,
            Long tripId,
            Long userId,
            String tripDate,
            String tripTime,
            String routeName,
            Integer minutesLeft
    ) implements Serializable {}
}
