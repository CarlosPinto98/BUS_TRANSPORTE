package com.unimag.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unimag.DTO.AssignmentDTO;
import com.unimag.DTO.TripDTO;
import com.unimag.entities.Enums.StatusTrip;
import com.unimag.service.AssignmentService;
import com.unimag.service.TripService;
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

import java.time.LocalDate;
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
class TripControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private TripService tripService;

    @Mock
    private AssignmentService assignmentService;

    @InjectMocks
    private TripController tripController;

    private TripDTO.tripResponse mockTripResponse;
    private TripDTO.tripCreateRequest mockCreateRequest;
    private TripDTO.tripUpdateRequest mockUpdateRequest;
    private LocalDate testDate;
    private LocalDateTime testDeparture;
    private LocalDateTime testArrival;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.standaloneSetup(tripController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testDate = LocalDate.of(2024, 12, 25);
        testDeparture = LocalDateTime.of(2024, 12, 25, 8, 0);
        testArrival = LocalDateTime.of(2024, 12, 25, 14, 0);

        mockTripResponse = new TripDTO.tripResponse(
                1L,
                testDate,
                testDeparture,
                testArrival,
                "SCHEDULED",
                100L,
                "Bogotá - Medellín",
                "Bogotá",
                "Medellín",
                200L,
                "ABC-123",
                40
        );

        mockCreateRequest = new TripDTO.tripCreateRequest(
                testDate,
                testDeparture,
                testArrival,
                100L,
                200L
        );

        mockUpdateRequest = new TripDTO.tripUpdateRequest(
                testDeparture,
                testArrival,
                200L,
                StatusTrip.SCHEDULED
        );
    }

    @Test
    @DisplayName("CREATE TRIP - Should return 400 when date is null")
    void createTrip_Fail_NullDate() throws Exception {

        TripDTO.tripCreateRequest invalidRequest = new TripDTO.tripCreateRequest(
                null, // date null
                testDeparture,
                testArrival,
                100L,
                200L
        );


        mockMvc.perform(post("/api/v1/trips/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(tripService, never()).createTrip(any());
    }

    @Test
    @DisplayName("CREATE TRIP - Should return 400 when date is in the past")
    void createTrip_Fail_PastDate() throws Exception {

        TripDTO.tripCreateRequest invalidRequest = new TripDTO.tripCreateRequest(
                LocalDate.of(2020, 1, 1), // fecha pasada
                testDeparture,
                testArrival,
                100L,
                200L
        );

        mockMvc.perform(post("/api/v1/trips/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(tripService, never()).createTrip(any());
    }

    @Test
    @DisplayName("UPDATE TRIP - Should update trip successfully")
    void updateTrip_Success() throws Exception {
        // Given
        Long tripId = 1L;
        TripDTO.tripResponse updatedResponse = new TripDTO.tripResponse(
                tripId,
                testDate,
                testDeparture.plusHours(1), // hora actualizada
                testArrival.plusHours(1),
                "BOARDING",
                100L, "Bogotá - Medellín", "Bogotá", "Medellín",
                200L, "ABC-123", 40
        );

        when(tripService.updateTrip(eq(tripId), any(TripDTO.tripUpdateRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/trips/update/{id}", tripId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tripId))
                .andExpect(jsonPath("$.statusTrip").value("BOARDING"));

        verify(tripService, times(1))
                .updateTrip(eq(tripId), any(TripDTO.tripUpdateRequest.class));
    }

    @Test
    @DisplayName("GET TRIP BY ID - Should retrieve trip by ID successfully")
    void getTripById_Success() throws Exception {

        Long tripId = 1L;
        when(tripService.getTripById(tripId)).thenReturn(mockTripResponse);

        mockMvc.perform(get("/api/v1/trips/search/{id}", tripId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tripId))
                .andExpect(jsonPath("$.routeName").value("Bogotá - Medellín"));

        verify(tripService, times(1)).getTripById(tripId);
    }

    @Test
    @DisplayName("GET TRIP WITH DETAILS - Should retrieve trip with details")
    void getTripWithDetails_Success() throws Exception {

        Long tripId = 1L;
        when(tripService.getTripWithDetails(tripId)).thenReturn(mockTripResponse);

        mockMvc.perform(get("/api/v1/trips/{id}/details", tripId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tripId));

        verify(tripService, times(1)).getTripWithDetails(tripId);
    }

    @Test
    @DisplayName("GET ALL TRIPS - Should retrieve all trips")
    void getAllTrips_Success() throws Exception {

        TripDTO.tripResponse trip1 = new TripDTO.tripResponse(
                1L, testDate, testDeparture, testArrival, "SCHEDULED",
                100L, "Route 1", "Origin 1", "Dest 1", 200L, "ABC-123", 40
        );
        TripDTO.tripResponse trip2 = new TripDTO.tripResponse(
                2L, testDate, testDeparture.plusHours(2), testArrival.plusHours(2), "BOARDING",
                101L, "Route 2", "Origin 2", "Dest 2", 201L, "XYZ-789", 45
        );

        List<TripDTO.tripResponse> trips = Arrays.asList(trip1, trip2);
        when(tripService.getAllTrips()).thenReturn(trips);

        // When & Then
        mockMvc.perform(get("/api/v1/trips/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(tripService, times(1)).getAllTrips();
    }

    @Test
    @DisplayName("SEARCH TRIPS - Should search trips with all parameters")
    void searchTrips_Success() throws Exception {

        Long routeId = 100L;
        StatusTrip status = StatusTrip.SCHEDULED;
        List<TripDTO.tripResponse> trips = Arrays.asList(mockTripResponse);

        when(tripService.searchTrips(routeId, testDate, status)).thenReturn(trips);

        mockMvc.perform(get("/api/v1/trips/search")
                        .param("routeId", String.valueOf(routeId))
                        .param("date", testDate.format(DateTimeFormatter.ISO_DATE))
                        .param("status", status.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].routeId").value(routeId));

        verify(tripService, times(1)).searchTrips(routeId, testDate, status);
    }

    @Test
    @DisplayName("SEARCH TRIPS - Should search with optional parameters")
    void searchTrips_OptionalParams() throws Exception {

        when(tripService.searchTrips(null, null, null)).thenReturn(List.of(mockTripResponse));

        mockMvc.perform(get("/api/v1/trips/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(tripService, times(1)).searchTrips(null, null, null);
    }

    @Test
    @DisplayName("GET TRIPS BY ROUTE AND DATE - Should retrieve trips")
    void getTripsByRouteAndDate_Success() throws Exception {

        Long routeId = 100L;
        List<TripDTO.tripResponse> trips = Arrays.asList(mockTripResponse);

        when(tripService.getTripsByRouteAndDate(routeId, testDate)).thenReturn(trips);

        mockMvc.perform(get("/api/v1/trips/route/{routeId}", routeId)
                        .param("date", testDate.format(DateTimeFormatter.ISO_DATE))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].routeId").value(routeId));

        verify(tripService, times(1)).getTripsByRouteAndDate(routeId, testDate);
    }

    @Test
    @DisplayName("GET ACTIVE TRIPS BY BUS - Should retrieve active trips for bus")
    void getActiveTripsByBus_Success() throws Exception {

        Long busId = 200L;
        List<TripDTO.tripResponse> trips = Arrays.asList(mockTripResponse);

        when(tripService.getActiveTripsByBus(busId, testDate)).thenReturn(trips);

        mockMvc.perform(get("/api/v1/trips/bus/{busId}", busId)
                        .param("date", testDate.format(DateTimeFormatter.ISO_DATE))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].busId").value(busId));

        verify(tripService, times(1)).getActiveTripsByBus(busId, testDate);
    }

    @Test
    @DisplayName("CHANGE TRIP STATUS - Should change trip status successfully")
    void changeTripStatus_Success() throws Exception {

        Long tripId = 1L;
        StatusTrip newStatus = StatusTrip.BOARDING;
        TripDTO.tripResponse updatedTrip = new TripDTO.tripResponse(
                tripId, testDate, testDeparture, testArrival, "BOARDING",
                100L, "Route", "Origin", "Dest", 200L, "ABC-123", 40
        );

        when(tripService.changeTripStatus(tripId, newStatus)).thenReturn(updatedTrip);

        mockMvc.perform(patch("/api/v1/trips/{id}/status", tripId)
                        .param("status", newStatus.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tripId))
                .andExpect(jsonPath("$.statusTrip").value("BOARDING"));

        verify(tripService, times(1)).changeTripStatus(tripId, newStatus);
    }

    @Test
    @DisplayName("OPEN BOARDING - Should open boarding successfully")
    void openBoarding_Success() throws Exception {

        Long tripId = 1L;
        TripDTO.tripResponse boardingTrip = new TripDTO.tripResponse(
                tripId, testDate, testDeparture, testArrival, "BOARDING",
                100L, "Route", "Origin", "Dest", 200L, "ABC-123", 40
        );

        when(tripService.changeTripStatus(tripId, StatusTrip.BOARDING)).thenReturn(boardingTrip);

        mockMvc.perform(post("/api/v1/trips/{id}/boarding/open", tripId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusTrip").value("BOARDING"));

        verify(tripService, times(1)).changeTripStatus(tripId, StatusTrip.BOARDING);
    }

    @Test
    @DisplayName("CLOSE BOARDING - Should close boarding successfully")
    void closeBoarding_Success() throws Exception {

        Long tripId = 1L;
        TripDTO.tripResponse departedTrip = new TripDTO.tripResponse(
                tripId, testDate, testDeparture, testArrival, "DEPARTED",
                100L, "Route", "Origin", "Dest", 200L, "ABC-123", 40
        );

        when(tripService.changeTripStatus(tripId, StatusTrip.DEPARTED)).thenReturn(departedTrip);

        mockMvc.perform(post("/api/v1/trips/{id}/boarding/close", tripId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusTrip").value("DEPARTED"));

        verify(tripService, times(1)).changeTripStatus(tripId, StatusTrip.DEPARTED);
    }

    @Test
    @DisplayName("MARK AS DEPARTED - Should mark trip as departed")
    void markAsDeparted_Success() throws Exception {

        Long tripId = 1L;
        TripDTO.tripResponse departedTrip = new TripDTO.tripResponse(
                tripId, testDate, testDeparture, testArrival, "DEPARTED",
                100L, "Route", "Origin", "Dest", 200L, "ABC-123", 40
        );

        when(tripService.changeTripStatus(tripId, StatusTrip.DEPARTED)).thenReturn(departedTrip);

        mockMvc.perform(post("/api/v1/trips/{id}/depart", tripId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusTrip").value("DEPARTED"));

        verify(tripService, times(1)).changeTripStatus(tripId, StatusTrip.DEPARTED);
    }

    @Test
    @DisplayName("MARK AS ARRIVED - Should mark trip as arrived")
    void markAsArrived_Success() throws Exception {

        Long tripId = 1L;
        TripDTO.tripResponse arrivedTrip = new TripDTO.tripResponse(
                tripId, testDate, testDeparture, testArrival, "ARRIVED",
                100L, "Route", "Origin", "Dest", 200L, "ABC-123", 40
        );

        when(tripService.changeTripStatus(tripId, StatusTrip.ARRIVED)).thenReturn(arrivedTrip);

        mockMvc.perform(post("/api/v1/trips/{id}/arrive", tripId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusTrip").value("ARRIVED"));

        verify(tripService, times(1)).changeTripStatus(tripId, StatusTrip.ARRIVED);
    }

    @Test
    @DisplayName("CANCEL TRIP - Should cancel trip successfully")
    void cancelTrip_Success() throws Exception {

        Long tripId = 1L;
        TripDTO.tripResponse cancelledTrip = new TripDTO.tripResponse(
                tripId, testDate, testDeparture, testArrival, "CANCELLED",
                100L, "Route", "Origin", "Dest", 200L, "ABC-123", 40
        );

        when(tripService.changeTripStatus(tripId, StatusTrip.CANCELLED)).thenReturn(cancelledTrip);

        mockMvc.perform(post("/api/v1/trips/{id}/cancel", tripId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusTrip").value("CANCELLED"));

        verify(tripService, times(1)).changeTripStatus(tripId, StatusTrip.CANCELLED);
    }

    @Test
    @DisplayName("REACTIVATE TRIP - Should reactivate cancelled trip")
    void reactivateTrip_Success() throws Exception {

        Long tripId = 1L;
        TripDTO.tripResponse reactivatedTrip = new TripDTO.tripResponse(
                tripId, testDate, testDeparture, testArrival, "SCHEDULED",
                100L, "Route", "Origin", "Dest", 200L, "ABC-123", 40
        );

        when(tripService.changeTripStatus(tripId, StatusTrip.SCHEDULED)).thenReturn(reactivatedTrip);

        mockMvc.perform(post("/api/v1/trips/{id}/reactivate", tripId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusTrip").value("SCHEDULED"));

        verify(tripService, times(1)).changeTripStatus(tripId, StatusTrip.SCHEDULED);
    }

    @Test
    @DisplayName("DELETE TRIP - Should delete trip successfully")
    void deleteTrip_Success() throws Exception {

        Long tripId = 1L;
        doNothing().when(tripService).deleteTrip(tripId);

        mockMvc.perform(delete("/api/v1/trips/delete/{id}", tripId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(tripService, times(1)).deleteTrip(tripId);
    }

    @Test
    @DisplayName("GET TODAY TRIPS - Should retrieve today's trips")
    void getTodayTrips_Success() throws Exception {

        LocalDate today = LocalDate.now();
        when(tripService.getTripsByRouteAndDate(null, today))
                .thenReturn(List.of(mockTripResponse));

        mockMvc.perform(get("/api/v1/trips/today")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(tripService, times(1)).getTripsByRouteAndDate(null, today);
    }

    @Test
    @DisplayName("GET TRIPS BY STATUS - Should retrieve trips by status")
    void getTripsByStatus_Success() throws Exception {

        StatusTrip status = StatusTrip.SCHEDULED;
        when(tripService.searchTrips(null, null, status))
                .thenReturn(List.of(mockTripResponse));

        mockMvc.perform(get("/api/v1/trips/status/{status}", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].statusTrip").value("SCHEDULED"));

        verify(tripService, times(1)).searchTrips(null, null, status);
    }

    @Test
    @DisplayName("GET TODAY ACTIVE TRIPS - Should retrieve today's active trips")
    void getTodayActiveTrips_Success() throws Exception {

        LocalDate today = LocalDate.now();
        when(tripService.searchTrips(null, today, null))
                .thenReturn(List.of(mockTripResponse));

        mockMvc.perform(get("/api/v1/trips/today/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(tripService, times(1)).searchTrips(null, today, null);
    }

    @Test
    @DisplayName("GET DRIVER TRIPS - Should handle missing authentication")
    void getDriverTrips_NoAuthentication() throws Exception {

        mockMvc.perform(get("/api/v1/trips/driver/my-trips")
                        .param("date", testDate.format(DateTimeFormatter.ISO_DATE))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(assignmentService, never()).getAssignmentsByDriverAndDate(anyLong(), any());
    }

    @Test
    @DisplayName("GET CURRENT DRIVER TRIPS - Should handle missing authentication")
    void getCurrentDriverTrips_NoAuthentication() throws Exception {

        mockMvc.perform(get("/api/v1/trips/driver/current-trips")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(assignmentService, never()).getAssignmentsByDriverAndDate(anyLong(), any());
    }

    @Test
    @DisplayName("GET ACTIVE DRIVER TRIPS - Should handle missing authentication")
    void getActiveDriverTrips_NoAuthentication() throws Exception {

        mockMvc.perform(get("/api/v1/trips/driver/active-trips")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(assignmentService, never()).getActiveAssignmentsByDriver(anyLong());
    }

    @Test
    @DisplayName("CREATE TRIP - Should create trip successfully")
    void createTrip_Success() throws Exception {

        when(tripService.createTrip(any(TripDTO.tripCreateRequest.class)))
                .thenReturn(mockTripResponse);

        mockMvc.perform(post("/api/v1/trips/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.routeId").value(100L))
                .andExpect(jsonPath("$.busId").value(200L))
                .andExpect(jsonPath("$.statusTrip").value("SCHEDULED"))
                .andExpect(jsonPath("$.routeName").value("Bogotá - Medellín"));

        verify(tripService, times(1)).createTrip(any(TripDTO.tripCreateRequest.class));
    }
}
