package com.unimag.service;

import com.unimag.DTO.IncidentDTO;
import com.unimag.entities.Enums.EntityType;
import com.unimag.entities.Enums.TypeIncident;

import java.time.LocalDateTime;
import java.util.List;

public interface IncidentService {

    IncidentDTO.incidentResponse create(IncidentDTO.incidentCreateRequest createRequest);
    IncidentDTO.incidentResponse update(Long id, IncidentDTO.incidentUpdateRequest updateRequest);
    IncidentDTO.incidentResponse getIncidentById(Long id);
    List<IncidentDTO.incidentResponse> getAll();
    List<IncidentDTO.incidentResponse> getIncidentsByEntityTypeAndId(EntityType entityType, Long entityId);
    List<IncidentDTO.incidentResponse> getIncidentsByType(TypeIncident type);
    List<IncidentDTO.incidentResponse> getIncidentsByReportedBy(Long reportedById);
    List<IncidentDTO.incidentResponse> getIncidentsByDateRange(LocalDateTime start, LocalDateTime end);
    void delete(Long id);
    long countIncidentsByType(TypeIncident type, LocalDateTime since);
}
