package com.unimag.mappers;

import com.unimag.DTO.AssignmentDTO.*;
import com.unimag.entities.Assignment;
import com.unimag.entities.Enums.StatusTrip;
import com.unimag.entities.Route;
import com.unimag.entities.Trip;
import com.unimag.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("AssignmentMapper Tests")
class AssignmentMapperTest {
    private AssignmentMapper assignmentMapper;

    @BeforeEach
    void setUp() {
        assignmentMapper = Mappers.getMapper(AssignmentMapper.class);
    }

    @Test
    @DisplayName("Debe mapear assignmentCreateRequest a la entidad Assignment")
    void toEntity() {

        assignmentCreateRequest request = new assignmentCreateRequest(
                1L,
                2L,
                3L
        );

        Assignment assignment = assignmentMapper.toEntity(request);

        assertNotNull(assignment);
        assertFalse(assignment.getChecklistOk());
        assertNull(assignment.getId());
    }

    @Test
    @DisplayName("Debe mapear la entidad Assignment a assignmentResponse")
    void toResponse() {

        Route route = Route.builder().id(1L)
                .origin("Bogotá")
                .destination("Tunja")
                .build();

        Trip trip = Trip.builder().id(1L)
                .date(LocalDate.of(2025, 12, 25))
                .departureAt(LocalDateTime.of(2025, 12, 25, 8, 0))
                .statusTrip(StatusTrip.SCHEDULED)
                .route(route)
                .build();

        User driver = User.builder().id(2L).name("Driver One").build();
        User dispatcher = User.builder().id(3L).name("Dispatcher One").build();
        LocalDateTime assignedAt = LocalDateTime.now();

        Assignment assignment = Assignment.builder()
                .id(1L)
                .trip(trip)
                .driver(driver)
                .dispatcher(dispatcher)
                .checklistOk(true)
                .assignedAt(assignedAt)
                .build();

        assignmentResponse response = assignmentMapper.toResponse(assignment);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertTrue(response.checklistOk());
        assertEquals(assignedAt, response.assignedAt());
        assertEquals(1L, response.tripId());
        assertNotNull(response.tripInfo());
        assertEquals("SCHEDULED", response.statusTrip());
        assertEquals(2L, response.driverId());
        assertEquals("Driver One", response.driverName());
        assertEquals(3L, response.dispatcherId());
        assertEquals("Dispatcher One", response.dispatcherName());
    }

}