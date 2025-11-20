package com.unimag.controllers;

import com.unimag.DTO.IncidentDTO;
import com.unimag.entities.Enums.EntityType;
import com.unimag.entities.Enums.TypeIncident;
import com.unimag.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/incidents")
@RequiredArgsConstructor
@Validated
@Slf4j

public class IncidentController {

    private final IncidentService incidentService;

    @PostMapping("/create")
    public ResponseEntity<IncidentDTO.incidentResponse> createIncident(@Valid @RequestBody IncidentDTO.incidentCreateRequest createRequest) {
        log.info("Creating incident - type: {}, entity: {} ({})",
                createRequest.typeIncident(), createRequest.entityType(), createRequest.entityId());

        IncidentDTO.incidentResponse created = incidentService.create(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/all")
    public ResponseEntity<List<IncidentDTO.incidentResponse>> getAllIncidents() {
        log.debug("Retrieving all incidents");

        List<IncidentDTO.incidentResponse> incidents = incidentService.getAll();
        return ResponseEntity.ok(incidents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentDTO.incidentResponse> getIncidentById(@PathVariable Long id) {
        log.debug("Retrieving incident: {}", id);

        IncidentDTO.incidentResponse incident = incidentService.getIncidentById(id);
        return ResponseEntity.ok(incident);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('DISPATCHER', 'ADMIN')")
    public ResponseEntity<IncidentDTO.incidentResponse> updateIncident(
            @PathVariable Long id,
            @Valid @RequestBody IncidentDTO.incidentUpdateRequest request
    ) {
        log.info("Updating incident: {}", id);

        IncidentDTO.incidentResponse updated = incidentService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id) {
        log.warn("Deleting incident: {}", id);

        incidentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== CONSULTAS ====================

    @GetMapping("/incident-entity")
    public ResponseEntity<List<IncidentDTO.incidentResponse>> getIncidentsByEntity(
            @RequestParam EntityType entityType,
            @RequestParam Long entityId
    ) {
        log.debug("Retrieving incidents for entity: {} ({})", entityType, entityId);

        List<IncidentDTO.incidentResponse> incidents = incidentService.getIncidentsByEntityTypeAndId(entityType, entityId);
        return ResponseEntity.ok(incidents);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<IncidentDTO.incidentResponse>> getIncidentsByType(@PathVariable TypeIncident type) {
        log.debug("Retrieving incidents of type: {}", type);

        List<IncidentDTO.incidentResponse> incidents = incidentService.getIncidentsByType(type);
        return ResponseEntity.ok(incidents);
    }

    @GetMapping("/reported-by/{reportedById}")
    public ResponseEntity<List<IncidentDTO.incidentResponse>> getIncidentsByReportedBy(@PathVariable Long reportedById) {
        log.debug("Retrieving incidents reported by user: {}", reportedById);

        List<IncidentDTO.incidentResponse> incidents = incidentService.getIncidentsByReportedBy(reportedById);
        return ResponseEntity.ok(incidents);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<IncidentDTO.incidentResponse>> getIncidentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        log.debug("Retrieving incidents between {} and {}", start, end);

        List<IncidentDTO.incidentResponse> incidents = incidentService.getIncidentsByDateRange(start, end);
        return ResponseEntity.ok(incidents);
    }

    @GetMapping("/type/{type}/count")
    @PreAuthorize("hasAnyRole('DISPATCHER', 'ADMIN')")
    public ResponseEntity<Long> countIncidentsByType(
            @PathVariable TypeIncident type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since
    ) {
        log.debug("Counting incidents of type {} since {}", type, since);

        long count = incidentService.countIncidentsByType(type, since);
        return ResponseEntity.ok(count);
    }
}
