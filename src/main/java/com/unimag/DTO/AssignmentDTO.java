package com.unimag.DTO;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;

public class AssignmentDTO {

    public record assignmentCreateRequest(
            @NotNull(message = "tripId is required")
            Long tripId,
            @NotNull(message = "driverId is required")
            Long driverId,
            @NotNull(message = "dispatcherId is required")
            Long dispatcherId
    ) implements Serializable {}

    public record assignmentUpdateRequest(
            @NotNull(message = "checklistOk is required")
            Boolean checklistOk,
            Long tripId,
            Long driverId,
            Long dispatcherId
    ) implements Serializable {}

    public record assignmentResponse(
            Long id,
            Boolean checklistOk,
            LocalDateTime assignedAt,
            Long tripId,
            String tripInfo,
            String statusTrip,
            Long driverId,
            String driverName,
            Long dispatcherId,
            String dispatcherName
    ) implements Serializable {}
}
