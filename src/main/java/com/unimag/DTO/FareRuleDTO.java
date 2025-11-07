package com.unimag.DTO;

import com.unimag.entities.Enums.DynamicPricing;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

public class FareRuleDTO {

    public record FareRuleCreateRequest(
            @NotNull @DecimalMin("0.0") BigDecimal basePrice,
            Map<String, Object> discounts,
            @NotNull DynamicPricing dynamicPricing,
            @NotNull Long routeId,
            @NotNull Long fromStopId,
            @NotNull Long toStopId
    ) implements Serializable {}

    public record FareRuleUpdateRequest(
            @DecimalMin("0.0") BigDecimal basePrice,
            Map<String, Object> discounts,
            DynamicPricing dynamicPricing
    ) implements Serializable {}

    public record FareRuleResponse(
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
