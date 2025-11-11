package com.unimag.mappers;

import com.unimag.DTO.IncidentDTO.*;
import com.unimag.entities.Enums.EntityType;
import com.unimag.entities.Enums.TypeIncident;
import com.unimag.entities.Incident;
import com.unimag.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("IncidentMapper Tests")
class IncidentMapperTest {

    private IncidentMapper incidentMapper;

    @BeforeEach
    void setUp() {
        incidentMapper = Mappers.getMapper(IncidentMapper.class);
    }

    @Test
    @DisplayName("Debe mapear incidentCreateRequest a la entidad Incident")
    void toEntity() {

        incidentCreateRequest request = new incidentCreateRequest(
                EntityType.TRIP,
                1L,
                TypeIncident.VEHICLE,
                "Tire puncture on route",
                2L
        );

        Incident incident = incidentMapper.toEntity(request);

        assertNotNull(incident);
        assertEquals(EntityType.TRIP, incident.getEntityType());
        assertEquals(1L, incident.getEntityId());
        assertEquals(TypeIncident.VEHICLE, incident.getTypeIncident());
        assertEquals("Tire puncture on route", incident.getNote());
        assertNull(incident.getId());
    }

    @Test
    void updateEntity() {
    }

    @Test
    @DisplayName("Debe mapear la entidad Incident a incidentResponse")
    void toResponse() {

        User reporter = User.builder().id(2L).name("Reporter User").build();
        LocalDateTime createdAt = LocalDateTime.now();

        Incident incident = Incident.builder()
                .id(1L)
                .entityType(EntityType.PARCEL)
                .entityId(5L)
                .typeIncident(TypeIncident.DELIVERY_FAIL)
                .note("Receiver not available")
                .reportedBy(reporter)
                .createdAt(createdAt)
                .build();

        incidentResponse response = incidentMapper.toResponse(incident);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("PARCEL", response.entityType());
        assertEquals(5L, response.entityId());
        assertEquals("DELIVERY_FAIL", response.type());
        assertEquals("Receiver not available", response.note());
        assertEquals(2L, response.reportedBy());
        assertEquals("Reporter User", response.reportedByName());
        assertEquals(createdAt, response.createdAt());
    }
}