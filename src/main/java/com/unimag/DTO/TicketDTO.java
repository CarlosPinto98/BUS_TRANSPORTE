package com.unimag.DTO;

import com.unimag.entities.Enums.PaymentMethod;
import com.unimag.entities.Enums.StatusTicket;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TicketDTO {

    public record ticketCreateRequest(
            @NotNull(message = "tripId is required")
            Long tripId,
            @NotNull(message = "passengerId is required")
            Long passengerId,
            @NotNull(message = "fromStopId is required")
            Long fromStopId,
            @NotNull(message = "toStopId is required")
            Long toStopId,
            @NotBlank(message = "seatNumber is required")
            @Size(max = 10, message = "seatNumber must not exceed 10 characters")
            String seatNumber,
            @NotNull(message = "paymentMethod is required")
            PaymentMethod paymentMethod) implements Serializable {}

    public record ticketUpdateRequest(
            @NotNull(message = "status is required")
            StatusTicket statusTicket) implements Serializable {}

    public record ticketResponse(
            Long id,
            Long tripId,
            String tripDate,
            String tripTime,
            Long passengerId,
            String passengerName,
            Long fromStopId,
            String fromStopName,
            Long toStopId,
            String toStopName,
            String seatNumber,
            BigDecimal price,
            String paymentMethod,
            String statusTicket,
            String qrCode,
            LocalDateTime createdAt) implements Serializable {}
}
