package com.unimag.mappers;

import com.unimag.DTO.StopDTO.*;
import com.unimag.entities.City;
import com.unimag.entities.Route;
import com.unimag.entities.Stop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("StopMapper Tests")
class StopMapperTest {

    private StopMapper stopMapper;

    @BeforeEach
    void setUp() {
        stopMapper = Mappers.getMapper(StopMapper.class);
    }

    @Test
    @DisplayName("Debe mapear stopCreateRequest a la entidad Stop")
    void toEntity() {


        stopCreateRequest request = new stopCreateRequest(
                "Terminal Bogotá",
                0,
                new BigDecimal("4.6533"),
                new BigDecimal("-74.0836"),
                1L,
                1L
        );

        Stop stop = stopMapper.toEntity(request);

        assertNotNull(stop);
        assertEquals("Terminal Bogotá", stop.getName());
        assertEquals(0, stop.getOrder());
        assertEquals(new BigDecimal("4.6533"), stop.getLat());
        assertEquals(new BigDecimal("-74.0836"), stop.getLng());
        assertNull(stop.getId());
        assertNotNull(stop.getRoute());
        assertEquals(1L, stop.getRoute().getId());
    }

    @Test
    @DisplayName("Debe actualizar la entidad Stop desde stopUpdateRequest")
    void updateEntity() {

        Stop existingStop = Stop.builder()
                .id(1L)
                .name("Old Name")
                .order(0)
                .lat(new BigDecimal("4.0000"))
                .lng(new BigDecimal("-74.0000"))
                .build();

        stopUpdateRequest request = new stopUpdateRequest(
                "New Terminal",
                1,
                1L
        );

        stopMapper.updateEntity(request, existingStop);

        assertEquals("New Terminal", existingStop.getName());
        assertEquals(1, existingStop.getOrder());
        // Verificamos que lat y lng NO cambiaron (solo se actualiza name, order y cityId)
        assertEquals(new BigDecimal("4.0000"), existingStop.getLat());
        assertEquals(new BigDecimal("-74.0000"), existingStop.getLng());
    }

    @Test
    @DisplayName("Debe mapear la entidad Stop a stopResponse")
    void toResponse() {

        Route route = Route.builder()
                .id(1L)
                .code("BOG-TUN")
                .name("Bogotá - Tunja")
                .build();

        Stop stop = Stop.builder()
                .id(1L)
                .name("Terminal Bogotá")
                .order(0)
                .lat(new BigDecimal("4.6533"))
                .lng(new BigDecimal("-74.0836"))
                .route(route)
                .build();

        stopResponse response = stopMapper.toResponse(stop);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Terminal Bogotá", response.name());
        assertEquals(0, response.order());
        assertEquals(new BigDecimal("4.6533"), response.lat());
        assertEquals(new BigDecimal("-74.0836"), response.lng());
        assertEquals(1L, response.routeId());
        assertEquals("Bogotá - Tunja", response.routeName());
        assertEquals("BOG-TUN", response.routeCode());
    }

    @Test
    @DisplayName("Debe mapear la referencia a Route correctamente")
    void mapRoute() {

        Long routeId = 42L;
        stopCreateRequest request = new stopCreateRequest(
                "Test Stop",
                0,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                routeId,
                1L
        );

        Stop stop = stopMapper.toEntity(request);

        assertNotNull(stop.getRoute());
        assertEquals(routeId, stop.getRoute().getId());
    }

    @Test
    @DisplayName("Debe preservar todos los campos durante el ciclo completo de mapeo")
    void shouldPreserveAllFieldsDuringFullCycleMapping() {

        stopCreateRequest request = new stopCreateRequest(
                "Complete Stop",
                5,
                new BigDecimal("10.1234567"),
                new BigDecimal("-75.9876543"),
                100L,
                1L
        );

        Stop stop = stopMapper.toEntity(request);
        stop.setId(999L);
        stop.setRoute(Route.builder()
                .id(100L)
                .code("TEST")
                .name("Test Route")
                .build());

        stopResponse response = stopMapper.toResponse(stop);

        assertEquals(999L, response.id());
        assertEquals("Complete Stop", response.name());
        assertEquals(5, response.order());
        assertEquals(new BigDecimal("10.1234567"), response.lat());
        assertEquals(new BigDecimal("-75.9876543"), response.lng());
        assertEquals(100L, response.routeId());
        assertEquals("Test Route", response.routeName());
    }

    @Test
    @DisplayName("Debe manejar coordenadas con alta precisión")
    void shouldHandleHighPrecisionCoordinates() {
        stopCreateRequest request = new stopCreateRequest(
                "Precise Location",
                0,
                new BigDecimal("4.6533278"),
                new BigDecimal("-74.0836333"),
                1L,
                1L
        );

        Stop stop = stopMapper.toEntity(request);

        assertNotNull(stop);
        assertEquals(new BigDecimal("4.6533278"), stop.getLat());
        assertEquals(new BigDecimal("-74.0836333"), stop.getLng());
    }

    @Test
    @DisplayName("Debe manejar diferentes órdenes de stop")
    void shouldHandleDifferentStopOrders() {
        for (int order = 0; order < 10; order++) {
            stopCreateRequest request = new stopCreateRequest(
                    "Stop " + order,
                    order,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    1L,
                    1L
            );

            Stop stop = stopMapper.toEntity(request);
            assertEquals(order, stop.getOrder());
        }
    }

    @Test
    @DisplayName("Debe manejar valores de latitud extremos")
    void shouldHandleExtremeLatitudeValues() {
        stopCreateRequest request1 = new stopCreateRequest(
                "North Pole",
                0,
                new BigDecimal("90.0"),
                BigDecimal.ZERO,
                1L,
                1L
        );

        Stop stop1 = stopMapper.toEntity(request1);

        assertEquals(new BigDecimal("90.0"), stop1.getLat());

        stopCreateRequest request2 = new stopCreateRequest(
                "South Pole",
                1,
                new BigDecimal("-90.0"),
                BigDecimal.ZERO,
                1L,
                1L
        );

        Stop stop2 = stopMapper.toEntity(request2);

        assertEquals(new BigDecimal("-90.0"), stop2.getLat());
    }

    @Test
    @DisplayName("Debe manejar valores de longitud extremos")
    void shouldHandleExtremeLongitudeValues() {
        stopCreateRequest request1 = new stopCreateRequest(
                "East Point",
                0,
                BigDecimal.ZERO,
                new BigDecimal("180.0"),
                1L,
                1L
        );

        Stop stop1 = stopMapper.toEntity(request1);
        assertEquals(new BigDecimal("180.0"), stop1.getLng());

        stopCreateRequest request2 = new stopCreateRequest(
                "West Point",
                1,
                BigDecimal.ZERO,
                new BigDecimal("-180.0"),
                1L,
                1L

        );

        Stop stop2 = stopMapper.toEntity(request2);
        assertEquals(new BigDecimal("-180.0"), stop2.getLng());
    }
}