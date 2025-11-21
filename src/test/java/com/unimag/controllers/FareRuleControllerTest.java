package com.unimag.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.DTO.FareRuleDTO;
import com.unimag.entities.Enums.DynamicPricing;
import com.unimag.service.FareRuleService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
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
class FareRuleControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private FareRuleService fareRuleService;

    @InjectMocks
    private FareRuleController fareRuleController;

    private FareRuleDTO.fareRuleResponse mockFareRuleResponse;
    private FareRuleDTO.fareRuleCreateRequest mockCreateRequest;
    private FareRuleDTO.fareRuleUpdateRequest mockUpdateRequest;
    private Map<String, Object> mockDiscounts;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.standaloneSetup(fareRuleController).build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        // Mock discounts
        mockDiscounts = new HashMap<>();
        mockDiscounts.put("student", 0.15);
        mockDiscounts.put("senior", 0.20);
        mockDiscounts.put("child", 0.30);

        // Mock response estándar
        mockFareRuleResponse = new FareRuleDTO.fareRuleResponse(
                1L,
                new BigDecimal("50000.00"),
                mockDiscounts,
                "OFF",
                100L,
                10L,
                20L,
                "Terminal Bogotá",
                "Terminal Medellín"
        );

        // Mock create request
        mockCreateRequest = new FareRuleDTO.fareRuleCreateRequest(
                new BigDecimal("50000.00"),
                mockDiscounts,
                DynamicPricing.OFF,
                100L, // routeId
                10L,  // fromStopId
                20L,  // toStopId
                1L,   // originId
                2L    // destinationId
        );

        // Mock update request
        mockUpdateRequest = new FareRuleDTO.fareRuleUpdateRequest(
                new BigDecimal("55000.00"),
                mockDiscounts,
                DynamicPricing.ON,
                1L,
                2L
        );
    }

//


    @Test
    @DisplayName("GET BY ID - Should retrieve fare rule by ID successfully")
    void get_Success() throws Exception {
        // Given
        Long fareRuleId = 1L;
        when(fareRuleService.get(fareRuleId)).thenReturn(mockFareRuleResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/fare-rules/{id}", fareRuleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fareRuleId))
                .andExpect(jsonPath("$.basePrice").value(50000.00))
                .andExpect(jsonPath("$.routeId").value(100L))
                .andExpect(jsonPath("$.fromStopId").value(10L))
                .andExpect(jsonPath("$.toStopId").value(20L))
                .andExpect(jsonPath("$.fromStopName").value("Terminal Bogotá"))
                .andExpect(jsonPath("$.toStopName").value("Terminal Medellín"));

        verify(fareRuleService, times(1)).get(fareRuleId);
    }


    @Test
    @DisplayName("DELETE - Should delete fare rule successfully")
    void delete_Success() throws Exception {
        // Given
        Long fareRuleId = 1L;
        when(fareRuleService.delete(fareRuleId)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/v1/fare-rules/delete/{id}", fareRuleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(fareRuleService, times(1)).delete(fareRuleId);
    }


    @Test
    @DisplayName("UPDATE - Should update fare rule successfully")
    void update_Success() throws Exception {
        // Given
        Long fareRuleId = 1L;
        FareRuleDTO.fareRuleResponse updatedResponse = new FareRuleDTO.fareRuleResponse(
                fareRuleId,
                new BigDecimal("55000.00"), // precio actualizado
                mockDiscounts,
                "ON", // dynamic pricing activado
                100L,
                10L,
                20L,
                "Terminal Bogotá",
                "Terminal Medellín"
        );

        when(fareRuleService.update(any(FareRuleDTO.fareRuleUpdateRequest.class), eq(fareRuleId)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/fare-rules/update/{id}", fareRuleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fareRuleId))
                .andExpect(jsonPath("$.basePrice").value(55000.00))
                .andExpect(jsonPath("$.dynamicPricing").value("ON"));

        verify(fareRuleService, times(1))
                .update(any(FareRuleDTO.fareRuleUpdateRequest.class), eq(fareRuleId));
    }

    @Test
    @DisplayName("UPDATE - Should return 400 when basePrice is negative")
    void update_Fail_NegativeBasePrice() throws Exception {
        // Given
        FareRuleDTO.fareRuleUpdateRequest invalidRequest = new FareRuleDTO.fareRuleUpdateRequest(
                new BigDecimal("-1000.00"), // precio negativo
                mockDiscounts,
                DynamicPricing.OFF,
                1L,
                2L
        );

        // When & Then
        mockMvc.perform(put("/api/v1/fare-rules/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(fareRuleService, never()).update(any(), anyLong());
    }

    @Test
    @DisplayName("UPDATE - Should handle partial update")
    void update_PartialUpdate() throws Exception {
        // Given
        Long fareRuleId = 1L;
        FareRuleDTO.fareRuleUpdateRequest partialRequest = new FareRuleDTO.fareRuleUpdateRequest(
                null, // solo actualizar dynamic pricing
                null,
                DynamicPricing.ON,
                null,
                null
        );

        FareRuleDTO.fareRuleResponse updatedResponse = new FareRuleDTO.fareRuleResponse(
                fareRuleId,
                new BigDecimal("50000.00"), // precio sin cambiar
                mockDiscounts,
                "ON", // solo cambió dynamic pricing
                100L, 10L, 20L,
                "Terminal Bogotá", "Terminal Medellín"
        );

        when(fareRuleService.update(any(FareRuleDTO.fareRuleUpdateRequest.class), eq(fareRuleId)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/fare-rules/update/{id}", fareRuleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dynamicPricing").value("ON"));

        verify(fareRuleService, times(1))
                .update(any(FareRuleDTO.fareRuleUpdateRequest.class), eq(fareRuleId));
    }

    @Test
    @DisplayName("CREATE FARE RULE - Should create fare rule successfully")
    void createFareRule_Success() throws Exception {
        // Given
        when(fareRuleService.createFareRule(any(FareRuleDTO.fareRuleCreateRequest.class)))
                .thenReturn(mockFareRuleResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/fare-rules/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.basePrice").value(50000.00))
                .andExpect(jsonPath("$.routeId").value(100L))
                .andExpect(jsonPath("$.fromStopId").value(10L))
                .andExpect(jsonPath("$.toStopId").value(20L))
                .andExpect(jsonPath("$.dynamicPricing").value("OFF"));

        verify(fareRuleService, times(1))
                .createFareRule(any(FareRuleDTO.fareRuleCreateRequest.class));
    }

    @Test
    @DisplayName("CREATE FARE RULE - Should return 400 when basePrice is null")
    void createFareRule_Fail_NullBasePrice() throws Exception {
        // Given
        FareRuleDTO.fareRuleCreateRequest invalidRequest = new FareRuleDTO.fareRuleCreateRequest(
                null, // basePrice null
                mockDiscounts,
                DynamicPricing.OFF,
                100L, 10L, 20L, 1L, 2L
        );

        // When & Then
        mockMvc.perform(post("/api/v1/fare-rules/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(fareRuleService, never()).createFareRule(any());
    }

    @Test
    @DisplayName("CREATE FARE RULE - Should return 400 when basePrice is negative")
    void createFareRule_Fail_NegativeBasePrice() throws Exception {
        // Given
        FareRuleDTO.fareRuleCreateRequest invalidRequest = new FareRuleDTO.fareRuleCreateRequest(
                new BigDecimal("-5000.00"), // precio negativo
                mockDiscounts,
                DynamicPricing.OFF,
                100L, 10L, 20L, 1L, 2L
        );

        // When & Then
        mockMvc.perform(post("/api/v1/fare-rules/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(fareRuleService, never()).createFareRule(any());
    }

    @Test
    @DisplayName("CREATE FARE RULE - Should return 400 when routeId is null")
    void createFareRule_Fail_NullRouteId() throws Exception {
        // Given
        FareRuleDTO.fareRuleCreateRequest invalidRequest = new FareRuleDTO.fareRuleCreateRequest(
                new BigDecimal("50000.00"),
                mockDiscounts,
                DynamicPricing.OFF,
                null, // routeId null
                10L, 20L, 1L, 2L
        );

        // When & Then
        mockMvc.perform(post("/api/v1/fare-rules/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(fareRuleService, never()).createFareRule(any());
    }

    @Test
    @DisplayName("CREATE FARE RULE - Should return 400 when dynamicPricing is null")
    void createFareRule_Fail_NullDynamicPricing() throws Exception {
        // Given
        FareRuleDTO.fareRuleCreateRequest invalidRequest = new FareRuleDTO.fareRuleCreateRequest(
                new BigDecimal("50000.00"),
                mockDiscounts,
                null, // dynamicPricing null
                100L, 10L, 20L, 1L, 2L
        );

        // When & Then
        mockMvc.perform(post("/api/v1/fare-rules/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(fareRuleService, never()).createFareRule(any());
    }

    @Test
    @DisplayName("CREATE FARE RULE - Should create with discounts map")
    void createFareRule_WithDiscounts() throws Exception {
        // Given
        Map<String, Object> complexDiscounts = new HashMap<>();
        complexDiscounts.put("student", 0.15);
        complexDiscounts.put("senior", 0.20);
        complexDiscounts.put("earlyBird", 0.10);
        complexDiscounts.put("groupSize", Map.of("min", 5, "discount", 0.25));

        FareRuleDTO.fareRuleCreateRequest requestWithDiscounts = new FareRuleDTO.fareRuleCreateRequest(
                new BigDecimal("50000.00"),
                complexDiscounts,
                DynamicPricing.OFF,
                100L, 10L, 20L, 1L, 2L
        );

        FareRuleDTO.fareRuleResponse responseWithDiscounts = new FareRuleDTO.fareRuleResponse(
                1L,
                new BigDecimal("50000.00"),
                complexDiscounts,
                "OFF",
                100L, 10L, 20L,
                "Terminal Bogotá", "Terminal Medellín"
        );

        when(fareRuleService.createFareRule(any(FareRuleDTO.fareRuleCreateRequest.class)))
                .thenReturn(responseWithDiscounts);

        // When & Then
        mockMvc.perform(post("/api/v1/fare-rules/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithDiscounts)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.discounts.student").value(0.15))
                .andExpect(jsonPath("$.discounts.senior").value(0.20));

        verify(fareRuleService, times(1))
                .createFareRule(any(FareRuleDTO.fareRuleCreateRequest.class));
    }

    @Test
    @DisplayName("CREATE FARE RULE - Should create with dynamic pricing ON")
    void createFareRule_WithDynamicPricingOn() throws Exception {
        // Given
        FareRuleDTO.fareRuleCreateRequest dynamicRequest = new FareRuleDTO.fareRuleCreateRequest(
                new BigDecimal("50000.00"),
                mockDiscounts,
                DynamicPricing.ON, // Dynamic pricing activado
                100L, 10L, 20L, 1L, 2L
        );

        FareRuleDTO.fareRuleResponse dynamicResponse = new FareRuleDTO.fareRuleResponse(
                1L,
                new BigDecimal("50000.00"),
                mockDiscounts,
                "ON",
                100L, 10L, 20L,
                "Terminal Bogotá", "Terminal Medellín"
        );

        when(fareRuleService.createFareRule(any(FareRuleDTO.fareRuleCreateRequest.class)))
                .thenReturn(dynamicResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/fare-rules/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dynamicRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dynamicPricing").value("ON"));

        verify(fareRuleService, times(1))
                .createFareRule(any(FareRuleDTO.fareRuleCreateRequest.class));
    }

//    @Test
//    @DisplayName("DELETE - Should handle service exception")
//    void delete_Fail_ServiceException() throws Exception {
//        // Given
//        Long fareRuleId = 999L;
//        when(fareRuleService.delete(fareRuleId))
//                .thenThrow(new RuntimeException("FareRule not found"));
//
//        // When & Then
//        mockMvc.perform(delete("/api/v1/fare-rules/delete/{id}", fareRuleId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().is5xxServerError());
//
//        verify(fareRuleService, times(1)).delete(fareRuleId);
//    }
//
//    @Test
//    @DisplayName("GET ALL - Should retrieve paginated fare rules")
//    void getAll_Success() throws Exception {
//
//        FareRuleDTO.fareRuleResponse fareRule1 = new FareRuleDTO.fareRuleResponse(
//                1L, new BigDecimal("50000.00"), mockDiscounts, "OFF",
//                100L, 10L, 20L, "Stop 1", "Stop 2"
//        );
//        FareRuleDTO.fareRuleResponse fareRule2 = new FareRuleDTO.fareRuleResponse(
//                2L, new BigDecimal("60000.00"), new HashMap<>(), "ON",
//                101L, 11L, 21L, "Stop 3", "Stop 4"
//        );
//
//        List<FareRuleDTO.fareRuleResponse> fareRules = Arrays.asList(fareRule1, fareRule2);
//        Page<FareRuleDTO.fareRuleResponse> page = new PageImpl<>(
//                fareRules, PageRequest.of(0, 10), fareRules.size()
//        );
//
//        when(fareRuleService.getAll(any(Pageable.class))).thenReturn(page);
//
//        // When & Then
//        mockMvc.perform(get("/api/v1/fare-rules/all")
//                        .param("page", "0")
//                        .param("size", "10")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content", hasSize(2)))
//                .andExpect(jsonPath("$.content[0].id").value(1L))
//                .andExpect(jsonPath("$.content[0].basePrice").value(50000.00))
//                .andExpect(jsonPath("$.content[0].dynamicPricing").value("OFF"))
//                .andExpect(jsonPath("$.content[1].id").value(2L))
//                .andExpect(jsonPath("$.content[1].dynamicPricing").value("ON"))
//                .andExpect(jsonPath("$.totalElements").value(2));
//
//        verify(fareRuleService, times(1)).getAll(any(Pageable.class));
//    }



//    @Test
//    @DisplayName("GET BY ID - Should return 500 when fare rule not found")
//    void get_NotFound() throws Exception {
//        // Given
//        Long fareRuleId = 999L;
//        when(fareRuleService.get(fareRuleId))
//                .thenThrow(new RuntimeException("FareRule not found"));
//
//        // When & Then
//        mockMvc.perform(get("/api/v1/fare-rules/{id}", fareRuleId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().is5xxServerError());
//
//        verify(fareRuleService, times(1)).get(fareRuleId);
//    }

//    @Test
//    @DisplayName("GET ALL - Should return empty page when no fare rules")
//    void getAll_EmptyPage() throws Exception {
//        // Given
//        Page<FareRuleDTO.fareRuleResponse> emptyPage = new PageImpl<>(
//                List.of(), PageRequest.of(0, 10), 0
//        );
//
//        when(fareRuleService.getAll(any(Pageable.class))).thenReturn(emptyPage);
//
//        // When & Then
//        mockMvc.perform(get("/api/v1/fare-rules/all")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content", hasSize(0)))
//                .andExpect(jsonPath("$.totalElements").value(0));
//
//        verify(fareRuleService, times(1)).getAll(any(Pageable.class));
//    }
}