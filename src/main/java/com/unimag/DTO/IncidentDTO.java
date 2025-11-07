package com.unimag.DTO;

import com.unimag.entities.Enums.EntityType;
import com.unimag.entities.Enums.TypeIncident;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;

public class IncidentDTO {

    public record IncidentCreateRequest(
            @NotBlank EntityType entityType,
            @NotNull Long entityId,
            @NotBlank TypeIncident type,
            String note,
            @NotNull Long reportedBy
    ) implements Serializable {}

    public record IncidentUpdateRequest(
            @NotBlank String note
    ) implements Serializable {}

    public record IncidentResponse(
            Long id,
            String entityType,
            Long entityId,
            String type,
            String note,
            Long reportedBy,
            String reporteByName,
            LocalDateTime createdAt
    ) implements Serializable {}
}
