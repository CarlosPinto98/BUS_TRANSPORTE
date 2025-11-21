package com.unimag.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.DTO.BusDTO;
import com.unimag.entities.Enums.StatusBus;
import com.unimag.service.BusService;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BusControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private BusService busService;

    @InjectMocks
    private BusController busController;

    private BusDTO.busResponse mockBusResponse;
    private BusDTO.busCreateRequest mockCreateRequest;
    private BusDTO.busUpdateRequest mockUpdateRequest;
    private Map<String, Object> mockAmenities;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.standaloneSetup(busController).build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        mockAmenities = new HashMap<>();
        mockAmenities.put("airConditioning", true);
        mockAmenities.put("wifi", true);
        mockAmenities.put("bathroom", true);
        mockAmenities.put("tv", false);

        mockBusResponse = new BusDTO.busResponse(
                1L,
                "ABC-123",
                40,
                "{\"airConditioning\":true,\"wifi\":true}",
                "ACTIVE"
        );

        mockCreateRequest = new BusDTO.busCreateRequest(
                "ABC-123",
                40,
                mockAmenities,
                StatusBus.ACTIVE
        );

        mockUpdateRequest = new BusDTO.busUpdateRequest(
                45,
                mockAmenities,
                StatusBus.MAINTENANCE
        );
    }

    @Test
    @DisplayName("CREATE BUS - Should create bus successfully")
    void createBus_Success() throws Exception {

        when(busService.createBus(any(BusDTO.busCreateRequest.class)))
                .thenReturn(mockBusResponse);

        mockMvc.perform(post("/api/v1/buses/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.plate").value("ABC-123"))
                .andExpect(jsonPath("$.capacity").value(40))
                .andExpect(jsonPath("$.statusBus").value("ACTIVE"));

        verify(busService, times(1)).createBus(any(BusDTO.busCreateRequest.class));
    }

    @Test
    @DisplayName("CREATE BUS - Should return 400 when plate is blank")
    void createBus_Fail_BlankPlate() throws Exception {

        BusDTO.busCreateRequest invalidRequest = new BusDTO.busCreateRequest(
                "", // plate vacía
                40,
                mockAmenities,
                StatusBus.ACTIVE
        );

        mockMvc.perform(post("/api/v1/buses/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(busService, never()).createBus(any());
    }

    @Test
    @DisplayName("CREATE BUS - Should return 400 when capacity is less than 1")
    void createBus_Fail_InvalidCapacity() throws Exception {

        BusDTO.busCreateRequest invalidRequest = new BusDTO.busCreateRequest(
                "ABC-123",
                0, // capacidad inválida
                mockAmenities,
                StatusBus.ACTIVE
        );

        mockMvc.perform(post("/api/v1/buses/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(busService, never()).createBus(any());
    }

    @Test
    @DisplayName("CREATE BUS - Should return 400 when status is null")
    void createBus_Fail_NullStatus() throws Exception {

        BusDTO.busCreateRequest invalidRequest = new BusDTO.busCreateRequest(
                "ABC-123",
                40,
                mockAmenities,
                null // status null
        );

        mockMvc.perform(post("/api/v1/buses/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(busService, never()).createBus(any());
    }

    @Test
    @DisplayName("GET ALL BUSES - Should retrieve all buses")
    void getAllBuses_Success() throws Exception {
        // Given
        BusDTO.busResponse bus1 = new BusDTO.busResponse(
                1L, "ABC-123", 40, "{}", "ACTIVE"
        );
        BusDTO.busResponse bus2 = new BusDTO.busResponse(
                2L, "XYZ-789", 45, "{}", "MAINTENANCE"
        );

        List<BusDTO.busResponse> buses = Arrays.asList(bus1, bus2);
        when(busService.getAllBuses()).thenReturn(buses);

        // When & Then
        mockMvc.perform(get("/api/v1/buses/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].plate").value("ABC-123"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].statusBus").value("MAINTENANCE"));

        verify(busService, times(1)).getAllBuses();
    }

    @Test
    @DisplayName("GET ALL BUSES - Should return empty list when no buses")
    void getAllBuses_EmptyList() throws Exception {
        // Given
        when(busService.getAllBuses()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v1/buses/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(busService, times(1)).getAllBuses();
    }

    @Test
    @DisplayName("GET BUS BY ID - Should retrieve bus by ID successfully")
    void getBusById_Success() throws Exception {
        // Given
        Long busId = 1L;
        when(busService.getBusById(busId)).thenReturn(mockBusResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/buses/{id}", busId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(busId))
                .andExpect(jsonPath("$.plate").value("ABC-123"))
                .andExpect(jsonPath("$.capacity").value(40));

        verify(busService, times(1)).getBusById(busId);
    }


    @Test
    @DisplayName("GET BUS WITH SEATS - Should retrieve bus with seats information")
    void getBusWithSeats_Success() throws Exception {
        // Given
        Long busId = 1L;
        when(busService.getBusWithSeats(busId)).thenReturn(mockBusResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/buses/{id}/with-seats", busId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(busId))
                .andExpect(jsonPath("$.plate").value("ABC-123"));

        verify(busService, times(1)).getBusWithSeats(busId);
    }

    @Test
    @DisplayName("GET BUS BY PLATE - Should retrieve bus by plate successfully")
    void getBusByPlate_Success() throws Exception {
        // Given
        String plate = "ABC-123";
        when(busService.getBusbyPlate(plate)).thenReturn(mockBusResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/buses/plate/{plate}", plate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plate").value(plate))
                .andExpect(jsonPath("$.id").value(1L));

        verify(busService, times(1)).getBusbyPlate(plate);
    }

    @Test
    @DisplayName("UPDATE BUS - Should update bus successfully")
    void updateBus_Success() throws Exception {
        // Given
        Long busId = 1L;
        BusDTO.busResponse updatedResponse = new BusDTO.busResponse(
                busId,
                "ABC-123",
                45, // capacidad actualizada
                "{}",
                "MAINTENANCE" // estado actualizado
        );

        when(busService.updateBus(eq(busId), any(BusDTO.busUpdateRequest.class)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/buses/update/{id}", busId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(busId))
                .andExpect(jsonPath("$.capacity").value(45))
                .andExpect(jsonPath("$.statusBus").value("MAINTENANCE"));

        verify(busService, times(1)).updateBus(eq(busId), any(BusDTO.busUpdateRequest.class));
    }

    @Test
    @DisplayName("UPDATE BUS - Should return 400 when capacity is less than 1")
    void updateBus_Fail_InvalidCapacity() throws Exception {
        // Given
        BusDTO.busUpdateRequest invalidRequest = new BusDTO.busUpdateRequest(
                0, // capacidad inválida
                mockAmenities,
                StatusBus.ACTIVE
        );

        // When & Then
        mockMvc.perform(put("/api/v1/buses/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(busService, never()).updateBus(anyLong(), any());
    }

    @Test
    @DisplayName("DELETE BUS - Should delete bus successfully")
    void deleteBus_Success() throws Exception {
        // Given
        Long busId = 1L;
        doNothing().when(busService).deleteBus(busId);

        // When & Then
        mockMvc.perform(delete("/api/v1/buses/delete/{id}", busId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(busService, times(1)).deleteBus(busId);
    }

    @Test
    @DisplayName("GET BUSES BY STATUS - Should retrieve buses by status")
    void getBusesByStatus_Success() throws Exception {
        // Given
        StatusBus status = StatusBus.ACTIVE;
        BusDTO.busResponse bus1 = new BusDTO.busResponse(
                1L, "ABC-123", 40, "{}", "ACTIVE"
        );
        BusDTO.busResponse bus2 = new BusDTO.busResponse(
                2L, "DEF-456", 45, "{}", "ACTIVE"
        );

        List<BusDTO.busResponse> buses = Arrays.asList(bus1, bus2);
        when(busService.getBusesByStatus(status)).thenReturn(buses);

        // When & Then
        mockMvc.perform(get("/api/v1/buses/status/{status}", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].statusBus").value("ACTIVE"))
                .andExpect(jsonPath("$[1].statusBus").value("ACTIVE"));

        verify(busService, times(1)).getBusesByStatus(status);
    }

    @Test
    @DisplayName("GET BUSES BY STATUS - Should return empty list when no buses with status")
    void getBusesByStatus_EmptyList() throws Exception {
        // Given
        StatusBus status = StatusBus.INACTIVE;
        when(busService.getBusesByStatus(status)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/v1/buses/status/{status}", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(busService, times(1)).getBusesByStatus(status);
    }

    @Test
    @DisplayName("GET AVAILABLE BUSES - Should retrieve available buses with min capacity")
    void getAvailableBuses_Success() throws Exception {
        // Given
        Integer minCapacity = 40;
        BusDTO.busResponse bus1 = new BusDTO.busResponse(
                1L, "ABC-123", 40, "{}", "ACTIVE"
        );
        BusDTO.busResponse bus2 = new BusDTO.busResponse(
                2L, "DEF-456", 45, "{}", "ACTIVE"
        );

        List<BusDTO.busResponse> buses = Arrays.asList(bus1, bus2);
        when(busService.getAvailableBuses(minCapacity)).thenReturn(buses);

        // When & Then
        mockMvc.perform(get("/api/v1/buses/available")
                        .param("minCapacity", String.valueOf(minCapacity))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].capacity").value(40))
                .andExpect(jsonPath("$[1].capacity").value(45));

        verify(busService, times(1)).getAvailableBuses(minCapacity);
    }

    @Test
    @DisplayName("GET AVAILABLE BUSES - Should use default minCapacity when not provided")
    void getAvailableBuses_DefaultMinCapacity() throws Exception {
        // Given
        when(busService.getAvailableBuses(1)).thenReturn(List.of(mockBusResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/buses/available")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(busService, times(1)).getAvailableBuses(1);
    }

    @Test
    @DisplayName("CHECK PLATE EXISTS - Should return true when plate exists")
    void checkPlateExists_True() throws Exception {
        // Given
        String plate = "ABC-123";
        when(busService.existsByPlate(plate)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/v1/buses/plate/{plate}/exists", plate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(busService, times(1)).existsByPlate(plate);
    }

    @Test
    @DisplayName("CHECK PLATE EXISTS - Should return false when plate does not exist")
    void checkPlateExists_False() throws Exception {
        // Given
        String plate = "NONEXISTENT";
        when(busService.existsByPlate(plate)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/v1/buses/plate/{plate}/exists", plate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));

        verify(busService, times(1)).existsByPlate(plate);
    }

    @Test
    @DisplayName("CHANGE BUS STATUS - Should change bus status successfully")
    void changeBusStatus_Success() throws Exception {
        // Given
        Long busId = 1L;
        StatusBus newStatus = StatusBus.MAINTENANCE;
        BusDTO.busResponse updatedResponse = new BusDTO.busResponse(
                busId,
                "ABC-123",
                40,
                "{}",
                "MAINTENANCE"
        );

        when(busService.changeBusStatus(busId, newStatus)).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(patch("/api/v1/buses/{id}/status", busId)
                        .param("status", newStatus.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(busId))
                .andExpect(jsonPath("$.statusBus").value("MAINTENANCE"));

        verify(busService, times(1)).changeBusStatus(busId, newStatus);
    }

    @Test
    @DisplayName("CHANGE BUS STATUS - Should handle all status transitions")
    void changeBusStatus_AllStatuses() throws Exception {
        // Given
        Long busId = 1L;

        // Test ACTIVE
        when(busService.changeBusStatus(eq(busId), eq(StatusBus.ACTIVE)))
                .thenReturn(new BusDTO.busResponse(busId, "ABC-123", 40, "{}", "ACTIVE"));

        mockMvc.perform(patch("/api/v1/buses/{id}/status", busId)
                        .param("status", StatusBus.ACTIVE.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusBus").value("ACTIVE"));

        // Test MAINTENANCE
        when(busService.changeBusStatus(eq(busId), eq(StatusBus.MAINTENANCE)))
                .thenReturn(new BusDTO.busResponse(busId, "ABC-123", 40, "{}", "MAINTENANCE"));

        mockMvc.perform(patch("/api/v1/buses/{id}/status", busId)
                        .param("status", StatusBus.MAINTENANCE.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusBus").value("MAINTENANCE"));

        // Test RETIRED
        when(busService.changeBusStatus(eq(busId), eq(StatusBus.INACTIVE)))
                .thenReturn(new BusDTO.busResponse(busId, "ABC-123", 40, "{}", "RETIRED"));

        mockMvc.perform(patch("/api/v1/buses/{id}/status", busId)
                        .param("status", StatusBus.INACTIVE.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusBus").value("RETIRED"));

        verify(busService, times(3)).changeBusStatus(eq(busId), any(StatusBus.class));
    }

    //    @Test
//    @DisplayName("DELETE BUS - Should handle service exception")
//    void deleteBus_Fail_ServiceException() throws Exception {
//        // Given
//        Long busId = 999L;
//        doThrow(new RuntimeException("Bus not found"))
//                .when(busService).deleteBus(busId);
//
//        // When & Then
//        mockMvc.perform(delete("/api/v1/buses/delete/{id}", busId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().is5xxServerError());
//
//        verify(busService, times(1)).deleteBus(busId);
//    }

//    @Test
//    @DisplayName("GET BUS BY ID - Should return 500 when bus not found")
//    void getBusById_NotFound() throws Exception {
//        // Given
//        Long busId = 999L;
//        when(busService.getBusById(busId))
//                .thenThrow(new RuntimeException("Bus not found"));
//
//        // When & Then
//        mockMvc.perform(get("/api/v1/buses/{id}", busId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().is5xxServerError());
//
//        verify(busService, times(1)).getBusById(busId);
//    }

//    @Test
//    @DisplayName("GET BUS BY PLATE - Should return 500 when plate not found")
//    void getBusByPlate_NotFound() throws Exception {
//        // Given
//        String plate = "INVALID-PLATE";
//        when(busService.getBusbyPlate(plate))
//                .thenThrow(new RuntimeException("Bus not found"));
//
//        // When & Then
//        mockMvc.perform(get("/api/v1/buses/plate/{plate}", plate)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().is5xxServerError());
//
//        verify(busService, times(1)).getBusbyPlate(plate);
//    }
}