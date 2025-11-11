package com.unimag.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class RouteDTO {

    public record routeCreateRequest(
            @NotBlank(message = "code is required")
            String code,
            @NotBlank(message = "name is required")
            String name,
            @NotBlank(message = "origin is required")
            String origin,
            @NotBlank(message = "destination is required")
            String destination,
            @NotNull @Min(1)
            Integer distanceKm,
            @NotNull @Min(1)
            Integer durationMin) implements Serializable {}

    public record routeUpdateRequest(
            @NotBlank String name,
            @Min(1) Integer distanceKm,
            @Min(1) Integer durationMin) implements Serializable {}

    public record routeResponse(
            Long id,
            String code,
            String name,
            String origin,
            String destination,
            Integer distanceKm,
            Integer durationMin,
            List<stopSummary> stops) implements Serializable {}

    public record stopSummary(
            Long id,
            String name,
            Integer order,
            BigDecimal lat,
            BigDecimal lng)implements Serializable{}
}
