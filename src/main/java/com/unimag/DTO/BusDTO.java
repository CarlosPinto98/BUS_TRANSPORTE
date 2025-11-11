package com.unimag.DTO;

import com.unimag.entities.Enums.StatusBus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Map;

public class BusDTO {

    public record busCreateRequest(
            @NotBlank(message = "plate is required")
            @Size(max = 20, message = "plate must not exceed 20 characters")
            String plate,
            @NotNull(message = "capacity is required")
            @Min(value = 1, message = "capacity must be at least 1")
            Integer capacity,
            Map<String, Object> amenities,
            @NotNull(message = "status is required")
            StatusBus statusBus
    ) implements Serializable {}

    public record busUpdateRequest(
            @Min(value = 1)
            Integer capacity,
            Map<String, Object> amenities,
            @NotNull StatusBus statusBus
    ) implements Serializable {}

    public record busResponse(
            Long id,
            String plate,
            Integer capacity,
            String amenities,
            String statusBus
    ) implements Serializable {}

    public record busWithSeatsResponse(
            Long id,
            String plate,
            Integer capacity,
            Map<String, Object> amenities,
            String statusBus,
            Integer totalSeats,
            Integer availableSeats
    ) implements Serializable {}
}
