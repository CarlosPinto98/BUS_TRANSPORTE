package com.unimag.DTO;

import com.unimag.entities.Enums.DynamicPricing;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

public class FareRuleDTO {

    public record fareRuleCreateRequest(
            @NotNull @DecimalMin("0.0") BigDecimal basePrice,
            Map<String, Object> discounts,
            @NotNull DynamicPricing dynamicPricing,
            @NotNull Long routeId,
            @NotNull Long fromStopId,
            @NotNull Long toStopId,
            @NotNull Long originId,
            @NotNull Long destinationId
    ) implements Serializable {}

    public record fareRuleUpdateRequest(
            @DecimalMin("0.0") BigDecimal basePrice,
            Map<String, Object> discounts,
            DynamicPricing dynamicPricing,
            Long originId,
            Long destinationId
    ) implements Serializable {}

    public record fareRuleResponse(
            Long id,
            BigDecimal basePrice,
            Map<String, Object> discounts,
            String dynamicPricing,
            Long routeId,
            Long fromStopId,
            Long toStopId,
            String fromStopName,
            String toStopName

    ) implements Serializable {}
}
