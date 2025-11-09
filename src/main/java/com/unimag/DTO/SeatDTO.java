package com.unimag.DTO;

import com.unimag.entities.Enums.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public class SeatDTO {

    public record SeatCreateRequest(
            @NotBlank(message = "number is required")
            @Size(max = 10, message = "number must not exceed 10 characters")
            String number,
            @NotNull(message = "type is required")
            Type type,
            @NotNull(message = "busId is required")
            Long busId) implements Serializable {}

    public record SeatUpdateRequest(
            @NotNull(message = "type is required")
            Type type) implements Serializable {}

    public record SeatResponse(
            Long id,
            String number,
            String type,
            Long busId,
            String busPlate,
            Integer busCapacity) implements Serializable {}
}
