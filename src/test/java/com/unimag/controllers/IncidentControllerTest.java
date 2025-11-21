package com.unimag.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unimag.DTO.IncidentDTO;
import com.unimag.entities.Enums.EntityType;
import com.unimag.entities.Enums.TypeIncident;
import com.unimag.service.IncidentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class IncidentControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private IncidentService incidentService;

    @InjectMocks
    private IncidentController incidentController;

    private IncidentDTO.incidentResponse mockIncidentResponse;
    private IncidentDTO.incidentCreateRequest mockCreateRequest;
    private IncidentDTO.incidentUpdateRequest mockUpdateRequest;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.standaloneSetup(incidentController).build();


        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testDateTime = LocalDateTime.of(2024, 1, 15, 10, 30);

        mockIncidentResponse = new IncidentDTO.incidentResponse(
                1L,
                "BUS",
                100L,
                "MECHANICAL",
                "Engine overheating detected",
                200L,
                "Juan Pérez",
                testDateTime
        );

        mockCreateRequest = new IncidentDTO.incidentCreateRequest(
                EntityType.TRIP,
                100L,
                TypeIncident.VEHICLE,
                "Engine overheating detected",
                200L
        );

        mockUpdateRequest = new IncidentDTO.incidentUpdateRequest(
                "Updated note: Issue resolved after coolant refill"
        );
    }

    @Test
    @DisplayName("CREATE INCIDENT - Should create incident successfully")
    void createIncident_Success() throws Exception {

        when(incidentService.create(any(IncidentDTO.incidentCreateRequest.class)))
                .thenReturn(mockIncidentResponse);

        mockMvc.perform(post("/api/v1/incidents/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.entityType").value("BUS"))
                .andExpect(jsonPath("$.entityId").value(100L))
                .andExpect(jsonPath("$.type").value("MECHANICAL"))
                .andExpect(jsonPath("$.note").value("Engine overheating detected"))
                .andExpect(jsonPath("$.reportedBy").value(200L));

        verify(incidentService, times(1)).create(any(IncidentDTO.incidentCreateRequest.class));
    }

    @Test
    @DisplayName("CREATE INCIDENT - Should return 400 when entityType is null")
    void createIncident_Fail_NullEntityType() throws Exception {

        IncidentDTO.incidentCreateRequest invalidRequest = new IncidentDTO.incidentCreateRequest(
                null, // entityType null
                100L,
                TypeIncident.VEHICLE,
                "Note",
                200L
        );

        mockMvc.perform(post("/api/v1/incidents/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(incidentService, never()).create(any());
    }

    @Test
    @DisplayName("CREATE INCIDENT - Should return 400 when entityId is null")
    void createIncident_Fail_NullEntityId() throws Exception {

        IncidentDTO.incidentCreateRequest invalidRequest = new IncidentDTO.incidentCreateRequest(
                EntityType.TRIP,
                null, // entityId null
                TypeIncident.VEHICLE,
                "Note",
                200L
        );

        mockMvc.perform(post("/api/v1/incidents/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(incidentService, never()).create(any());
    }

    @Test
    @DisplayName("CREATE INCIDENT - Should return 400 when typeIncident is null")
    void createIncident_Fail_NullTypeIncident() throws Exception {

        IncidentDTO.incidentCreateRequest invalidRequest = new IncidentDTO.incidentCreateRequest(
                EntityType.TRIP,
                100L,
                null, // typeIncident null
                "Note",
                200L
        );

        mockMvc.perform(post("/api/v1/incidents/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(incidentService, never()).create(any());
    }

    @Test
    @DisplayName("CREATE INCIDENT - Should create incident without note")
    void createIncident_WithoutNote() throws Exception {

        IncidentDTO.incidentCreateRequest requestWithoutNote = new IncidentDTO.incidentCreateRequest(
                EntityType.DRIVER,
                150L,
                TypeIncident.SECURITY,
                null, // note opcional
                200L
        );

        IncidentDTO.incidentResponse responseWithoutNote = new IncidentDTO.incidentResponse(
                2L, "DRIVER", 150L, "BEHAVIORAL", null, 200L, "Juan Pérez", testDateTime
        );

        when(incidentService.create(any(IncidentDTO.incidentCreateRequest.class)))
                .thenReturn(responseWithoutNote);

        mockMvc.perform(post("/api/v1/incidents/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithoutNote)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.note").isEmpty());

        verify(incidentService, times(1)).create(any(IncidentDTO.incidentCreateRequest.class));
    }

    @Test
    @DisplayName("GET ALL INCIDENTS - Should retrieve all incidents")
    void getAllIncidents_Success() throws Exception {

        IncidentDTO.incidentResponse incident1 = new IncidentDTO.incidentResponse(
                1L, "BUS", 100L, "MECHANICAL", "Issue 1", 200L, "User 1", testDateTime
        );
        IncidentDTO.incidentResponse incident2 = new IncidentDTO.incidentResponse(
                2L, "DRIVER", 150L, "BEHAVIORAL", "Issue 2", 201L, "User 2", testDateTime
        );

        List<IncidentDTO.incidentResponse> incidents = Arrays.asList(incident1, incident2);
        when(incidentService.getAll()).thenReturn(incidents);

        mockMvc.perform(get("/api/v1/incidents/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].entityType").value("BUS"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].entityType").value("DRIVER"));

        verify(incidentService, times(1)).getAll();
    }

    @Test
    @DisplayName("GET ALL INCIDENTS - Should return empty list when no incidents")
    void getAllIncidents_EmptyList() throws Exception {

        when(incidentService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/incidents/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(incidentService, times(1)).getAll();
    }

    @Test
    @DisplayName("GET INCIDENT BY ID - Should retrieve incident by ID successfully")
    void getIncidentById_Success() throws Exception {
        // Given
        Long incidentId = 1L;
        when(incidentService.getIncidentById(incidentId)).thenReturn(mockIncidentResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/incidents/{id}", incidentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(incidentId))
                .andExpect(jsonPath("$.entityType").value("BUS"))
                .andExpect(jsonPath("$.type").value("MECHANICAL"));

        verify(incidentService, times(1)).getIncidentById(incidentId);
    }

//    @Test
//    @DisplayName("GET INCIDENT BY ID - Should return 500 when incident not found")
//    void getIncidentById_NotFound() throws Exception {
//        // Given
//        Long incidentId = 999L;
//        when(incidentService.getIncidentById(incidentId))
//                .thenThrow(new RuntimeException("Incident not found"));
//
//        // When & Then
//        mockMvc.perform(get("/api/v1/incidents/{id}", incidentId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().is5xxServerError());
//
//        verify(incidentService, times(1)).getIncidentById(incidentId);
//    }

    @Test
    @DisplayName("UPDATE INCIDENT - Should update incident successfully")
    void updateIncident_Success() throws Exception {
        // Given
        Long incidentId = 1L;
        IncidentDTO.incidentResponse updatedResponse = new IncidentDTO.incidentResponse(
                incidentId,
                "BUS",
                100L,
                "MECHANICAL",
                "Updated note: Issue resolved after coolant refill",
                200L,
                "Juan Pérez",
                testDateTime
        );

        when(incidentService.update(eq(incidentId), any(IncidentDTO.incidentUpdateRequest.class)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/incidents/update/{id}", incidentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(incidentId))
                .andExpect(jsonPath("$.note").value(containsString("Updated note")));

        verify(incidentService, times(1))
                .update(eq(incidentId), any(IncidentDTO.incidentUpdateRequest.class));
    }

    @Test
    @DisplayName("UPDATE INCIDENT - Should return 400 when note is blank")
    void updateIncident_Fail_BlankNote() throws Exception {
        // Given
        IncidentDTO.incidentUpdateRequest invalidRequest = new IncidentDTO.incidentUpdateRequest(
                "" // note vacío
        );

        // When & Then
        mockMvc.perform(put("/api/v1/incidents/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(incidentService, never()).update(anyLong(), any());
    }

    @Test
    @DisplayName("DELETE INCIDENT - Should delete incident successfully")
    void deleteIncident_Success() throws Exception {
        // Given
        Long incidentId = 1L;
        doNothing().when(incidentService).delete(incidentId);

        // When & Then
        mockMvc.perform(delete("/api/v1/incidents/delete/{id}", incidentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(incidentService, times(1)).delete(incidentId);
    }

//    @Test
//    @DisplayName("DELETE INCIDENT - Should handle service exception")
//    void deleteIncident_Fail_ServiceException() throws Exception {
//        // Given
//        Long incidentId = 999L;
//        doThrow(new RuntimeException("Incident not found"))
//                .when(incidentService).delete(incidentId);
//
//        // When & Then
//        mockMvc.perform(delete("/api/v1/incidents/delete/{id}", incidentId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().is5xxServerError());
//
//        verify(incidentService, times(1)).delete(incidentId);
//    }

    @Test
    @DisplayName("GET INCIDENTS BY ENTITY - Should retrieve incidents by entity type and ID")
    void getIncidentsByEntity_Success() throws Exception {
        // Given
        EntityType entityType = EntityType.TRIP;
        Long entityId = 100L;

        IncidentDTO.incidentResponse incident1 = new IncidentDTO.incidentResponse(
                1L, "BUS", 100L, "MECHANICAL", "Issue 1", 200L, "User 1", testDateTime
        );
        IncidentDTO.incidentResponse incident2 = new IncidentDTO.incidentResponse(
                2L, "BUS", 100L, "MAINTENANCE", "Issue 2", 201L, "User 2", testDateTime
        );

        List<IncidentDTO.incidentResponse> incidents = Arrays.asList(incident1, incident2);
        when(incidentService.getIncidentsByEntityTypeAndId(entityType, entityId))
                .thenReturn(incidents);

        // When & Then
        mockMvc.perform(get("/api/v1/incidents/incident-entity")
                        .param("entityType", entityType.name())
                        .param("entityId", String.valueOf(entityId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].entityType").value("BUS"))
                .andExpect(jsonPath("$[0].entityId").value(100L))
                .andExpect(jsonPath("$[1].entityType").value("BUS"))
                .andExpect(jsonPath("$[1].entityId").value(100L));

        verify(incidentService, times(1))
                .getIncidentsByEntityTypeAndId(entityType, entityId);
    }

    @Test
    @DisplayName("GET INCIDENTS BY ENTITY - Should handle different entity types")
    void getIncidentsByEntity_DifferentTypes() throws Exception {
        // Test DRIVER
        when(incidentService.getIncidentsByEntityTypeAndId(EntityType.DRIVER, 150L))
                .thenReturn(List.of(new IncidentDTO.incidentResponse(
                        1L, "DRIVER", 150L, "BEHAVIORAL", "Issue", 200L, "User", testDateTime
                )));

        mockMvc.perform(get("/api/v1/incidents/incident-entity")
                        .param("entityType", "DRIVER")
                        .param("entityId", "150"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].entityType").value("DRIVER"));

        // Test TRIP
        when(incidentService.getIncidentsByEntityTypeAndId(EntityType.TRIP, 200L))
                .thenReturn(List.of(new IncidentDTO.incidentResponse(
                        2L, "TRIP", 200L, "DELAY", "Issue", 200L, "User", testDateTime
                )));

        mockMvc.perform(get("/api/v1/incidents/incident-entity")
                        .param("entityType", "TRIP")
                        .param("entityId", "200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].entityType").value("TRIP"));
    }

    @Test
    @DisplayName("GET INCIDENTS BY TYPE - Should retrieve incidents by type")
    void getIncidentsByType_Success() throws Exception {
        // Given
        TypeIncident type = TypeIncident.MECHANICAL;

        IncidentDTO.incidentResponse incident1 = new IncidentDTO.incidentResponse(
                1L, "BUS", 100L, "MECHANICAL", "Issue 1", 200L, "User 1", testDateTime
        );
        IncidentDTO.incidentResponse incident2 = new IncidentDTO.incidentResponse(
                2L, "BUS", 101L, "MECHANICAL", "Issue 2", 201L, "User 2", testDateTime
        );

        List<IncidentDTO.incidentResponse> incidents = Arrays.asList(incident1, incident2);
        when(incidentService.getIncidentsByType(type)).thenReturn(incidents);

        // When & Then
        mockMvc.perform(get("/api/v1/incidents/type/{type}", type)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].type").value("MECHANICAL"))
                .andExpect(jsonPath("$[1].type").value("MECHANICAL"));

        verify(incidentService, times(1)).getIncidentsByType(type);
    }

    @Test
    @DisplayName("GET INCIDENTS BY TYPE - Should return empty list for type with no incidents")
    void getIncidentsByType_EmptyList() throws Exception {
        // Given
        TypeIncident type = TypeIncident.ACCIDENT;
        when(incidentService.getIncidentsByType(type)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v1/incidents/type/{type}", type)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(incidentService, times(1)).getIncidentsByType(type);
    }

    @Test
    @DisplayName("GET INCIDENTS BY REPORTED BY - Should retrieve incidents by reporter")
    void getIncidentsByReportedBy_Success() throws Exception {
        // Given
        Long reporterId = 200L;

        IncidentDTO.incidentResponse incident1 = new IncidentDTO.incidentResponse(
                1L, "BUS", 100L, "MECHANICAL", "Issue 1", 200L, "Juan Pérez", testDateTime
        );
        IncidentDTO.incidentResponse incident2 = new IncidentDTO.incidentResponse(
                2L, "DRIVER", 150L, "BEHAVIORAL", "Issue 2", 200L, "Juan Pérez", testDateTime
        );

        List<IncidentDTO.incidentResponse> incidents = Arrays.asList(incident1, incident2);
        when(incidentService.getIncidentsByReportedBy(reporterId)).thenReturn(incidents);

        // When & Then
        mockMvc.perform(get("/api/v1/incidents/reported-by/{reportedById}", reporterId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].reportedBy").value(200L))
                .andExpect(jsonPath("$[1].reportedBy").value(200L));

        verify(incidentService, times(1)).getIncidentsByReportedBy(reporterId);
    }

    @Test
    @DisplayName("GET INCIDENTS BY DATE RANGE - Should retrieve incidents in date range")
    void getIncidentsByDateRange_Success() throws Exception {
        // Given
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);

        IncidentDTO.incidentResponse incident1 = new IncidentDTO.incidentResponse(
                1L, "BUS", 100L, "MECHANICAL", "Issue 1", 200L, "User 1",
                LocalDateTime.of(2024, 1, 15, 10, 0)
        );
        IncidentDTO.incidentResponse incident2 = new IncidentDTO.incidentResponse(
                2L, "DRIVER", 150L, "BEHAVIORAL", "Issue 2", 201L, "User 2",
                LocalDateTime.of(2024, 1, 20, 14, 30)
        );

        List<IncidentDTO.incidentResponse> incidents = Arrays.asList(incident1, incident2);
        when(incidentService.getIncidentsByDateRange(start, end)).thenReturn(incidents);

        // When & Then
        mockMvc.perform(get("/api/v1/incidents/date-range")
                        .param("start", start.format(DateTimeFormatter.ISO_DATE_TIME))
                        .param("end", end.format(DateTimeFormatter.ISO_DATE_TIME))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(incidentService, times(1)).getIncidentsByDateRange(start, end);
    }

    @Test
    @DisplayName("COUNT INCIDENTS BY TYPE - Should count incidents by type since date")
    void countIncidentsByType_Success() throws Exception {
        // Given
        TypeIncident type = TypeIncident.MECHANICAL;
        LocalDateTime since = LocalDateTime.of(2024, 1, 1, 0, 0);
        long count = 15L;

        when(incidentService.countIncidentsByType(type, since)).thenReturn(count);

        // When & Then
        mockMvc.perform(get("/api/v1/incidents/type/{type}/count", type)
                        .param("since", since.format(DateTimeFormatter.ISO_DATE_TIME))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(15));

        verify(incidentService, times(1)).countIncidentsByType(type, since);
    }

    @Test
    @DisplayName("COUNT INCIDENTS BY TYPE - Should return zero when no incidents")
    void countIncidentsByType_Zero() throws Exception {
        // Given
        TypeIncident type = TypeIncident.ACCIDENT;
        LocalDateTime since = LocalDateTime.of(2024, 1, 1, 0, 0);

        when(incidentService.countIncidentsByType(type, since)).thenReturn(0L);

        // When & Then
        mockMvc.perform(get("/api/v1/incidents/type/{type}/count", type)
                        .param("since", since.format(DateTimeFormatter.ISO_DATE_TIME))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(0));

        verify(incidentService, times(1)).countIncidentsByType(type, since);
    }
}