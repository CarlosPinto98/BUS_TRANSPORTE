package com.unimag.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.DTO.AssignmentDTO.*;
import com.unimag.security.JWT.JwtUtil;
import com.unimag.security.service.JwtService;
import com.unimag.service.AssignmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(
        controllers = AssignmentController.class,
        excludeAutoConfiguration = {SpringDataWebAutoConfiguration.class}
)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AssignmentControllerTest {

    @MockitoBean
    private JwtUtil jwtUtil;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AssignmentService assignmentService;
    @MockitoBean
    private JwtService jwtService;

    private assignmentResponse response;
    private assignmentCreateRequest createRequest;
    private assignmentUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {

        response = new assignmentResponse(
                1L,
                false,
                LocalDateTime.now(),
                100L,
                "Bogotá - Medellín",
                "SCHEDULED",
                200L,
                "Juan Pérez",
                300L,
                "María García"
        );

        createRequest = new assignmentCreateRequest(
                100L, // tripId
                200L, // driverId
                300L  // dispatcherId
        );

        updateRequest = new assignmentUpdateRequest(
                true,  // checklistOk
                100L,  // tripId
                200L,  // driverId
                300L   // dispatcherId
        );
    }

    @Test
    @DisplayName("CREATE - Should create assignment successfully")
    void create() throws Exception {

        when(assignmentService.create(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/assignments/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tripId").value(100L))
                .andExpect(jsonPath("$.driverId").value(200L))
                .andExpect(jsonPath("$.dispatcherId").value(300L))
                .andExpect(jsonPath("$.checklistOk").value(false))
                .andExpect(jsonPath("$.tripInfo").value("Bogotá - Medellín"));

        verify(assignmentService, times(1))
                .create(any());
    }

    @Test
    @DisplayName("CREATE - Should return 400 when tripId is null")
    void create_Fail_NullTripId() throws Exception {

        assignmentResponse invalidRequest = new assignmentResponse(
                null, false, LocalDateTime.now(), null, "Info", "SCHEDULED",
                200L, "Driver", 300L, "Dispatcher"
        );

        mockMvc.perform(post("/api/v1/assignments/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(assignmentService, never()).create(any());
    }

    @Test
    @DisplayName("UPDATE - Should update assignment successfully")
    void update_Success() throws Exception {


        Long assignmentId = 1L;
        assignmentResponse updatedResponse = new assignmentResponse(
                assignmentId,
                true, // checklistOk actualizado
                LocalDateTime.now(),
                100L,
                "Bogotá - Medellín",
                "SCHEDULED",
                200L,
                "Juan Pérez",
                300L,
                "María García"
        );

        when(assignmentService.update(eq(assignmentId), any(assignmentUpdateRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/assignments/update/{id}", assignmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(assignmentId))
                .andExpect(jsonPath("$.checklistOk").value(true))
                .andExpect(jsonPath("$.tripId").value(100L));

        verify(assignmentService, times(1)).update(eq(assignmentId), any(assignmentUpdateRequest.class));
    }


    @Test
    @DisplayName("GET ALL - Should retrieve paginated assignments")
    void getAll_Success() throws Exception {
        // Given
        assignmentResponse assignment1 = new assignmentResponse(
                1L, false, LocalDateTime.now(), 100L, "Ruta 1", "SCHEDULED",
                200L, "Driver 1", 300L, "Dispatcher 1"
        );
        assignmentResponse assignment2 = new assignmentResponse(
                2L, true, LocalDateTime.now(), 101L, "Ruta 2", "BOARDING",
                201L, "Driver 2", 301L, "Dispatcher 2"
        );

        List<assignmentResponse> assignments = Arrays.asList(assignment1, assignment2);
        Page<assignmentResponse> page = new PageImpl<>(assignments, PageRequest.of(0, 10), assignments.size());

        when(assignmentService.getAll(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/assignments/all")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(assignmentService, times(1)).getAll(any(Pageable.class));
    }


    @Test
    @DisplayName("DELETE - Should delete assignment successfully (with ADMIN role)")
    void delete_Success() throws Exception {
        // Given
        Long assignmentId = 1L;
        doNothing().when(assignmentService).delete(assignmentId);

        // When & Then
        mockMvc.perform(delete("/api/v1/assignments/delete/{id}", assignmentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(assignmentService, times(1)).delete(assignmentId);
    }


    @Test
    @DisplayName("GET BY DRIVER - Should retrieve assignment by driver ID")
    void get_Success() throws Exception {

        Long driverId = 200L;
        when(assignmentService.get(driverId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/assignments/driver/{driverId}", driverId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.driverId").value(driverId))
                .andExpect(jsonPath("$.driverName").value("Juan Pérez"));

        verify(assignmentService, times(1)).get(driverId);
    }

    @Test
    @DisplayName("GET BY DRIVER - Should handle non-existent driver")
    void get_NotFound() throws Exception {

        Long driverId = 999L;
        when(assignmentService.get(driverId))
                .thenThrow(new RuntimeException("Driver not found"));

        mockMvc.perform(get("/api/v1/assignments/driver/{driverId}", driverId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is5xxServerError());

        verify(assignmentService, times(1)).get(driverId);
    }

    protected String mapToJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }
}