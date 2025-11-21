package com.unimag.controllers;

import com.unimag.DTO.RouteDTO;
import com.unimag.DTO.StopDTO;
import com.unimag.service.RouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("🛣️ Pruebas Unitarias del Controlador de Rutas (RouteController)")
class RouteControllerTest {

    private StopDTO.cityDTO city;

    @Mock
    private RouteService routeService;

    @InjectMocks
    private RouteController routeController;

    private RouteDTO.routeResponse response;
    private RouteDTO.routeCreateRequest createRequest;
    private RouteDTO.routeUpdateRequest updateRequest;
    private StopDTO.stopResponse stopResponse;


    @BeforeEach
    void setUp() {

        RouteDTO.stopSummary mockStopSummary = new RouteDTO.stopSummary(
                10L, "Stop A", 1, new BigDecimal("10.0"), new BigDecimal("20.0"));


        response = new RouteDTO.routeResponse(
                1L, "RTA-001", "Ruta Principal", "Origen", "Destino",
                100, 120, Collections.singletonList(mockStopSummary)
        );

        createRequest = new RouteDTO.routeCreateRequest(
                "RTA-NEW", "Ruta Nueva", "City A", "City B",
                200, 240, 1L, 2L
        );


        updateRequest = new RouteDTO.routeUpdateRequest(
                "Ruta Modificada", 150, 180
        );

        stopResponse = new StopDTO.stopResponse(
                10L, "Stop Name", 1, new BigDecimal("10.0"), new BigDecimal("20.0"),
                1L, "Ruta Principal", "RT-002", city
        );
    }

    @Test
    @DisplayName("✅ GET /all: Debería retornar una lista de rutas con estado ")
    void getAllRoutes_shouldReturnListOfRoutesAndOkStatus() {

        List<RouteDTO.routeResponse> mockList = Collections.singletonList(response);
        when(routeService.getAllRoutes()).thenReturn(mockList);

        ResponseEntity<List<RouteDTO.routeResponse>> response = routeController.getAllRoutes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(routeService, times(1)).getAllRoutes();
    }

    @Test
    @DisplayName("🔍 GET /{id}: Debería retornar una ruta por ID con estado 200 OK")
    void getRouteById_shouldReturnRouteResponseAndOkStatus() {

        Long routeId = 1L;
        when(routeService.getRouteById(routeId)).thenReturn(response);

        ResponseEntity<RouteDTO.routeResponse> response = routeController.getRouteById(routeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(routeId, response.getBody().id());
        verify(routeService, times(1)).getRouteById(routeId);
    }

    @Test
    @DisplayName("📍 GET /{id}/stops: Debería retornar una lista de paradas de la ruta con estado 200 OK")
    void getRouteStops_shouldReturnListOfStopsAndOkStatus() {

        Long routeId = 1L;
        List<StopDTO.stopResponse> mockStops = Collections.singletonList(stopResponse);
        when(routeService.getStopsByRoute(routeId)).thenReturn(mockStops);

        ResponseEntity<List<StopDTO.stopResponse>> response = routeController.getRouteStops(routeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(routeService, times(1)).getStopsByRoute(routeId);
    }

    @Test
    @DisplayName("🔎 GET /search: Debería retornar rutas al buscar por Origen y Destino (200 OK)")
    void searchRoutes_shouldReturnMatchingRoutesAndOkStatus() {

        String origin = "Origen";
        String destination = "Destino";
        List<RouteDTO.routeResponse> mockList = Collections.singletonList(response);
        when(routeService.searchRoutes(origin, destination)).thenReturn(mockList);

        ResponseEntity<List<RouteDTO.routeResponse>> response = routeController.searchRoutes(origin, destination);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(routeService, times(1)).searchRoutes(origin, destination);
    }

    @Test
    @DisplayName("🔑 GET /code/{code}: Debería retornar una ruta por código (RTA-001) con estado 200 OK")
    void getRouteByCode_shouldReturnRouteResponseAndOkStatus() {

        String code = "RTA-001";
        when(routeService.getRouteByCode(code)).thenReturn(response);

        ResponseEntity<RouteDTO.routeResponse> response = routeController.getRouteByCode(code);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(code, response.getBody().code());
        verify(routeService, times(1)).getRouteByCode(code);
    }

    @Test
    @DisplayName("🗺️ GET /{id}/with-stops: Debería retornar la ruta con sus paradas cargadas (200 OK)")
    void getRouteWithStops_shouldReturnRouteWithStopsAndOkStatus() {

        Long routeId = 1L;
        when(routeService.getRouteWithStops(routeId)).thenReturn(response);

        ResponseEntity<RouteDTO.routeResponse> response = routeController.getRouteWithStops(routeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().stops().isEmpty()); // Verifica que las paradas no estén vacías
        verify(routeService, times(1)).getRouteWithStops(routeId);
    }

    @Test
    @DisplayName("➕ POST /create: Debería crear una nueva ruta y retornar 201 CREATED")
    void createRoute_shouldReturnCreatedRouteAndCreatedStatus() {

        when(routeService.createRoute(createRequest)).thenReturn(response);

        ResponseEntity<RouteDTO.routeResponse> response = routeController.createRoute(createRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("RTA-001", response.getBody().code());
        verify(routeService, times(1)).createRoute(createRequest);
    }

    @Test
    @DisplayName("🔄 PUT /update/{id}: Debería actualizar la ruta y retornar 200 OK")
    void updateRoute_shouldReturnUpdatedRouteAndOkStatus() {

        Long routeId = 1L;

        RouteDTO.routeResponse updatedResponse = new RouteDTO.routeResponse(
                routeId, response.code(), updateRequest.name(),
                response.origin(), response.destination(),
                updateRequest.distanceKm(), updateRequest.durationMin(),
                response.stops()
        );

        when(routeService.updateRoute(routeId, updateRequest)).thenReturn(updatedResponse);

        ResponseEntity<RouteDTO.routeResponse> response = routeController.updateRoute(routeId, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedResponse.name(), response.getBody().name());
        verify(routeService, times(1)).updateRoute(routeId, updateRequest);
    }

    @Test
    @DisplayName("🗑️ DELETE /delete/{id}: Debería llamar al servicio para eliminar y retornar 204 NO CONTENT")
    void deleteRoute_shouldCallServiceAndDeleteAndReturnNoContentStatus() {

        Long routeId = 1L;

        doNothing().when(routeService).deleteRoute(routeId);

        ResponseEntity<Void> response = routeController.deleteRoute(routeId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody()); // El cuerpo de la respuesta debe ser nulo
        verify(routeService, times(1)).deleteRoute(routeId);
    }

    @Test
    @DisplayName("❓ GET /code/{code}/exists: Debería retornar 'true' si el código existe (200 OK)")
    void checkCodeExists_shouldReturnTrueAndOkStatus() {

        String code = "RTA-001";
        when(routeService.existsByCode(code)).thenReturn(true);

        ResponseEntity<Boolean> response = routeController.checkCodeExists(code);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(routeService, times(1)).existsByCode(code);
    }
}

