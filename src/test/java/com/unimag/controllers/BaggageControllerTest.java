package com.unimag.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.DTO.BaggageDTO;
import com.unimag.service.BaggageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
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
class BaggageControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private BaggageService baggageService;

    @InjectMocks
    private BaggageController baggageController;

    private BaggageDTO.baggageResponse mockBaggageResponse;
    private BaggageDTO.baggageCreateRequest mockCreateRequest;
    private BaggageDTO.baggageUpdateRequest mockUpdateRequest;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.standaloneSetup(baggageController).build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        mockBaggageResponse = new BaggageDTO.baggageResponse(
                1L,
                new BigDecimal("15.50"),
                new BigDecimal("25000.00"),
                "BAG-001-2024",
                100L,
                "Juan Pérez",
                "Bogotá - Medellín",
                false
        );

        mockCreateRequest = new BaggageDTO.baggageCreateRequest(
                100L, // ticketId
                new BigDecimal("15.50") // weightKg
        );

        mockUpdateRequest = new BaggageDTO.baggageUpdateRequest(
                new BigDecimal("30000.00") // fee
        );
    }

    @Test
    @DisplayName("CREATE - Should create baggage successfully")
    void create_Success() throws Exception {
        
        when(baggageService.create(any(BaggageDTO.baggageCreateRequest.class)))
                .thenReturn(mockBaggageResponse);

        mockMvc.perform(post("/api/v1/baggage/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.weightKg").value(15.50))
                .andExpect(jsonPath("$.fee").value(25000.00))
                .andExpect(jsonPath("$.tagCode").value("BAG-001-2024"))
                .andExpect(jsonPath("$.ticketId").value(100L))
                .andExpect(jsonPath("$.passengerName").value("Juan Pérez"))
                .andExpect(jsonPath("$.excessWeight").value(false));

        verify(baggageService, times(1)).create(any(BaggageDTO.baggageCreateRequest.class));
    }

    @Test
    @DisplayName("CREATE - Should return 400 when ticketId is null")
    void create_Fail_NullTicketId() throws Exception {
        // Given
        BaggageDTO.baggageCreateRequest invalidRequest = new BaggageDTO.baggageCreateRequest(
                null, // ticketId null
                new BigDecimal("15.50")
        );

        // When & Then
        mockMvc.perform(post("/api/v1/baggage/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(baggageService, never()).create(any());
    }

    @Test
    @DisplayName("CREATE - Should return 400 when weight is negative")
    void create_Fail_NegativeWeight() throws Exception {
        // Given
        BaggageDTO.baggageCreateRequest invalidRequest = new BaggageDTO.baggageCreateRequest(
                100L,
                new BigDecimal("-5.00") // peso negativo
        );

        // When & Then
        mockMvc.perform(post("/api/v1/baggage/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(baggageService, never()).create(any());
    }

    @Test
    @DisplayName("GET ALL - Should retrieve paginated baggage")
    void getAll_Success() throws Exception {
        // Given
        BaggageDTO.baggageResponse baggage1 = new BaggageDTO.baggageResponse(
                1L, new BigDecimal("15.50"), new BigDecimal("25000.00"),
                "BAG-001", 100L, "Passenger 1", "Route 1", false
        );
        BaggageDTO.baggageResponse baggage2 = new BaggageDTO.baggageResponse(
                2L, new BigDecimal("22.00"), new BigDecimal("35000.00"),
                "BAG-002", 101L, "Passenger 2", "Route 2", true
        );

        List<BaggageDTO.baggageResponse> baggageList = Arrays.asList(baggage1, baggage2);
        Page<BaggageDTO.baggageResponse> page = new PageImpl<>(
                baggageList, PageRequest.of(0, 10), baggageList.size()
        );

        when(baggageService.getAll(any(PageRequest.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/baggage/all")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].tagCode").value("BAG-001"))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[1].excessWeight").value(true))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(baggageService, times(1)).getAll(any(PageRequest.class));
    }



    @Test
    @DisplayName("GET BY ID - Should retrieve baggage by ID successfully")
    void get_ById_Success() throws Exception {
        // Given
        Long baggageId = 1L;
        when(baggageService.get(baggageId)).thenReturn(mockBaggageResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/baggage/{id}", baggageId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(baggageId))
                .andExpect(jsonPath("$.weightKg").value(15.50))
                .andExpect(jsonPath("$.tagCode").value("BAG-001-2024"));

        verify(baggageService, times(1)).get(baggageId);
    }



    @Test
    @DisplayName("GET BY TAG CODE - Should retrieve baggage by tag code successfully")
    void get_ByTagCode_Success() throws Exception {
        // Given
        String tagCode = "BAG-001-2024";
        when(baggageService.get(tagCode)).thenReturn(mockBaggageResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/baggage/tag/{tagCode}", tagCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagCode").value(tagCode))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.passengerName").value("Juan Pérez"));

        verify(baggageService, times(1)).get(tagCode);
    }

    @Test
    @DisplayName("GET BY TAG CODE - Should return 500 when tag code not found")
    void get_ByTagCode_NotFound() throws Exception {
        // Given
        String tagCode = "INVALID-TAG";
        when(baggageService.get(tagCode))
                .thenThrow(new RuntimeException("Baggage not found"));

        // When & Then
        mockMvc.perform(get("/api/v1/baggage/tag/{tagCode}", tagCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is5xxServerError());

        verify(baggageService, times(1)).get(tagCode);
    }

    @Test
    @DisplayName("UPDATE - Should update baggage successfully")
    void update_Success() throws Exception {
        // Given
        Long baggageId = 1L;
        BaggageDTO.baggageResponse updatedResponse = new BaggageDTO.baggageResponse(
                baggageId,
                new BigDecimal("15.50"),
                new BigDecimal("30000.00"), // fee actualizada
                "BAG-001-2024",
                100L,
                "Juan Pérez",
                "Bogotá - Medellín",
                false
        );

        when(baggageService.update(any(BaggageDTO.baggageUpdateRequest.class), eq(baggageId)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/baggage/update/{id}", baggageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(baggageId))
                .andExpect(jsonPath("$.fee").value(30000.00));

        verify(baggageService, times(1))
                .update(any(BaggageDTO.baggageUpdateRequest.class), eq(baggageId));
    }

    @Test
    @DisplayName("UPDATE - Should return 400 when fee is negative")
    void update_Fail_NegativeFee() throws Exception {
        // Given
        BaggageDTO.baggageUpdateRequest invalidRequest = new BaggageDTO.baggageUpdateRequest(
                new BigDecimal("-100.00") // fee negativa
        );

        // When & Then
        mockMvc.perform(put("/api/v1/baggage/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(baggageService, never()).update(any(), anyLong());
    }

    @Test
    @DisplayName("DELETE - Should delete baggage successfully")
    void delete_Success() throws Exception {
        // Given
        Long baggageId = 1L;
        when(baggageService.delete(baggageId)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/v1/baggage/delete/{id}", baggageId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(baggageService, times(1)).delete(baggageId);
    }



    @Test
    @DisplayName("GET OBJECT - Should retrieve baggage list by trip ID")
    void getObject_Success() throws Exception {
        
        Long tripId = 500L;
        BaggageDTO.baggageResponse baggage1 = new BaggageDTO.baggageResponse(
                1L, new BigDecimal("15.50"), new BigDecimal("25000.00"),
                "BAG-001", 100L, "Passenger 1", "Trip Info", false
        );
        BaggageDTO.baggageResponse baggage2 = new BaggageDTO.baggageResponse(
                2L, new BigDecimal("18.00"), new BigDecimal("28000.00"),
                "BAG-002", 101L, "Passenger 2", "Trip Info", false
        );

        List<BaggageDTO.baggageResponse> baggageList = Arrays.asList(baggage1, baggage2);

        when(baggageService.getObject(tripId)).thenReturn(null);

        when(baggageService.getObject(tripId)).thenAnswer(invocation -> baggageList);

        mockMvc.perform(get("/api/v1/baggage/trip/{tripId}", tripId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].tagCode").value("BAG-001"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].tagCode").value("BAG-002"));

        verify(baggageService, times(1)).getObject(tripId);
    }

}