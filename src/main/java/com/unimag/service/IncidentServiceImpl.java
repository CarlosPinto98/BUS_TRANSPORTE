package com.unimag.service;

import com.unimag.DTO.IncidentDTO;
import com.unimag.entities.Enums.EntityType;
import com.unimag.entities.Enums.TypeIncident;
import com.unimag.entities.Incident;
import com.unimag.entities.User;
import com.unimag.mappers.IncidentMapper;
import com.unimag.repository.IncidentRepository;
import com.unimag.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional

public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;
    private final IncidentMapper incidentMapper;

    @Override
    public IncidentDTO.incidentResponse create(IncidentDTO.incidentCreateRequest createRequest) {
        User reportedBy = userRepository.findById(createRequest.reportedBy())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + createRequest.reportedBy()));

        Incident incident = incidentMapper.toEntity(createRequest);
        incident.setReportedBy(reportedBy);

        Incident savedIncident = incidentRepository.save(incident);
        return incidentMapper.toResponse(savedIncident);
    }

    @Override
    public IncidentDTO.incidentResponse update(Long id, IncidentDTO.incidentUpdateRequest updateRequest) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + id));

        incidentMapper.updateEntity(updateRequest, incident);
        Incident updatedIncident = incidentRepository.save(incident);
        return incidentMapper.toResponse(updatedIncident);
    }

    @Override
    public IncidentDTO.incidentResponse getIncidentById(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + id));
        return incidentMapper.toResponse(incident);
    }

    @Override
    public List<IncidentDTO.incidentResponse> getAll() {
        return incidentRepository.findAll().stream()
                .map(incidentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<IncidentDTO.incidentResponse> getIncidentsByEntityTypeAndId(EntityType entityType, Long entityId) {
        return incidentRepository.findByEntityOrderByCreatedAtDesc(entityType, entityId).stream()
                .map(incidentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<IncidentDTO.incidentResponse> getIncidentsByType(TypeIncident type) {
        return incidentRepository.findByTypeIncident(type).stream()
                .map(incidentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<IncidentDTO.incidentResponse> getIncidentsByReportedBy(Long reportedById) {
        if (!userRepository.existsById(reportedById)) {
            throw new IllegalArgumentException("User not found: " + reportedById);
        }
        return incidentRepository.findByReportedById(reportedById).stream()
                .map(incidentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<IncidentDTO.incidentResponse> getIncidentsByDateRange(LocalDateTime start, LocalDateTime end) {
        return incidentRepository.findByDateRange(start, end).stream()
                .map(incidentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!incidentRepository.existsById(id)) {
            throw new IllegalArgumentException("Incident not found: " + id);
        }
        incidentRepository.deleteById(id);
    }

    @Override
    public long countIncidentsByType(TypeIncident type, LocalDateTime since) {
        return incidentRepository.countByTypeAndCreatedAtAfter(type, since);
    }
}
