package com.unimag.DTO;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ConfigDTO {

    public record configCreateRequest(
            @NotBlank(message = "key is required")
            String key,
            @NotBlank(message = "value is required")
            String value,
            String description
    ) implements Serializable {}

    public record configUpdateRequest(
            @NotBlank(message = "value is required")
            String value,
            String description
    ) implements Serializable {}

    public record configResponse(
            Long id,
            String key,
            String value,
            String description,
            LocalDateTime updatedAt
    ) implements Serializable {}
}
