package com.unimag.controllers;

import com.unimag.DTO.StopDTO;
import com.unimag.service.StopService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("📍 Pruebas Unitarias del Controlador de Paradas (StopController)")
class StopControllerTest {

    @Mock
    private StopService stopService;

    @InjectMocks
    private StopController stopController;

    private StopDTO.stopResponse response;
    private StopDTO.stopCreateRequest createRequest;
    private StopDTO.stopUpdateRequest updateRequest;

    private final Long STOP_ID = 1L;
    private final Long ROUTE_ID = 10L;
    private final Long CITY_ID = 5L;

    @BeforeEach
    void setUp() {

        response = new StopDTO.stopResponse(
                1L,
                "Bquilla-Norte",
                1,
                new BigDecimal("11.00"),
                new BigDecimal("-74.00"),
                1L,
                "Ruta Norte",
                "RN-001",
                new StopDTO.cityDTO("SANTA MARTA")
        );

        createRequest = new StopDTO.stopCreateRequest(
                "Bquilla-Norte",
                1,
                new BigDecimal("11.00"),
                new BigDecimal("-74.00"),
                ROUTE_ID,
                CITY_ID
        );

        updateRequest = new StopDTO.stopUpdateRequest(
                "Bquilla-Centro",
                2,
                CITY_ID
        );
    }

    @Test
    @DisplayName("➕ POST /create: Debería crear una nueva parada y retornar ")
    void create_shouldReturnCreatedStopAndCreatedStatus() {

        when(stopService.create(createRequest)).thenReturn(response);

        ResponseEntity<StopDTO.stopResponse> response = stopController.create(createRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(STOP_ID, response.getBody().id());
        verify(stopService, times(1)).create(createRequest);
    }

    @Test
    @DisplayName("🔄 PUT /update/{id}: Debería actualizar la parada y retornar")
    void updateStop_shouldReturnUpdatedStopAndOkStatus() {

        StopDTO.stopResponse updatedResponse = new StopDTO.stopResponse(
                STOP_ID,
                "Bquilla-Centro",
                2,
                new BigDecimal("11.00"),
                new BigDecimal("-74.00"),
                1L,
                "Ruta Norte",
                "RN-001",
                new StopDTO.cityDTO("SANTA MARTA")
        );

        when(stopService.updateStop(STOP_ID, updateRequest)).thenReturn(updatedResponse);

        ResponseEntity<StopDTO.stopResponse> response = stopController.updateStop(STOP_ID, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Bquilla-Centro", response.getBody().name());
        verify(stopService, times(1)).updateStop(STOP_ID, updateRequest);
    }

    @Test
    @DisplayName("🗑️ DELETE /delete/{id}: Debería eliminar la parada y retornar ")
    void delete_shouldCallServiceAndDeleteAndReturnNoContentStatus() {

        when(stopService.delete(STOP_ID)).thenReturn(true);

        ResponseEntity<Void> response = stopController.delete(STOP_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(stopService, times(1)).delete(STOP_ID);
    }

    @Test
    @DisplayName("✅ GET all (Pageable): Debería retornar una página de paradas ")
    void getAll_shouldReturnPageOfStopsAndOkStatus() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<StopDTO.stopResponse> mockPage = new PageImpl<>(
                Collections.singletonList(response), pageable, 1);

        when(stopService.getAll(pageable)).thenReturn(mockPage);

        ResponseEntity<Page<StopDTO.stopResponse>> response = stopController.getAll(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(stopService, times(1)).getAll(pageable);
    }

    @Test
    @DisplayName("📋 GET all (List): Debería retornar una lista de todas las paradas ")
    void getAllStops_shouldReturnListOfAllStops() {

        List<StopDTO.stopResponse> mockList = Collections.singletonList(response);
        when(stopService.getAllStops()).thenReturn(mockList);

        ResponseEntity<List<StopDTO.stopResponse>> response = stopController.getAllStops();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(stopService, times(1)).getAllStops();
    }

    @Test
    @DisplayName("🔍 GET /{id}: Debería retornar una parada específica por ID ")
    void getStopById_shouldReturnStopResponseAndOkStatus() {

        when(stopService.getStopById(STOP_ID)).thenReturn(response);

        ResponseEntity<StopDTO.stopResponse> response = stopController.getStopById(STOP_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(STOP_ID, response.getBody().id());
        verify(stopService, times(1)).getStopById(STOP_ID);
    }

    @Test
    @DisplayName("🗺️ GET /route/{routeId}: Debería retornar las paradas de una ruta específica ")
    void getStopsByRoute_shouldReturnListOfStopsForRoute() {

        List<StopDTO.stopResponse> mockList = Collections.singletonList(response);
        when(stopService.getStopsByRouteId(ROUTE_ID)).thenReturn(mockList);

        ResponseEntity<List<StopDTO.stopResponse>> response = stopController.getStopsByRoute(ROUTE_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(stopService, times(1)).getStopsByRouteId(ROUTE_ID);
    }
}
