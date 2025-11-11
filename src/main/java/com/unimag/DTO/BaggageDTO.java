package com.unimag.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;

public class BaggageDTO {

    public record baggageCreateRequest(
            @NotNull(message = "ticketId is required")
            Long ticketId,
            @NotNull @DecimalMin(value = "0.0", message = "weight must be positive")
            BigDecimal weightKg
    ) implements Serializable {}

    public record baggageUpdateRequest(
            @DecimalMin("0.0") BigDecimal fee
    ) implements Serializable {}

    public record baggageResponse(
            Long id,
            BigDecimal weightKg,
            BigDecimal fee,
            String tagCode,
            Long ticketId,
            String passengerName,
            String tripInfo,
            Boolean excessWeight
    ) implements Serializable {}
}
