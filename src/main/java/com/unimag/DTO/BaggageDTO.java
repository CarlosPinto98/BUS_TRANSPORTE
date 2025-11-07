package com.unimag.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;

public class BaggageDTO {

    public record BaggageCreateRequest(
            @NotNull(message = "ticketId is required")
            Long ticketId,
            @NotNull @DecimalMin(value = "0.0", message = "weight must be positive")
            BigDecimal weightKg,
            @NotBlank(message = "tagCode is required")
            String tagCode
    ) implements Serializable {}

    public record BaggageUpdateRequest(
            @DecimalMin("0.0") BigDecimal fee
    ) implements Serializable {}

    public record BaggageResponse(
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
