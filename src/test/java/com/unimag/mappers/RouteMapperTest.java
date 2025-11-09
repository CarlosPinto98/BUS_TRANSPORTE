package com.unimag.mappers;

import com.unimag.DTO.RouteDTO.*;
import com.unimag.entities.Route;
import com.unimag.entities.Stop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("RouteMapper Tests")
class RouteMapperTest {

    private RouteMapper routeMapper;

    @BeforeEach
    void setUp() {
        routeMapper = Mappers.getMapper(RouteMapper.class);
    }

    @Test
    @DisplayName("Debe mapear RouteCreateRequest a la entidad Route")
    void toEntity() {

        RouteCreateRequest request = new RouteCreateRequest(
                "BOG-TUN",
                "Bogotá - Tunja",
                "Bogotá",
                "Tunja",
                150,
                180
        );

        Route route = routeMapper.toEntity(request);

        assertNotNull(route);
        assertEquals("BOG-TUN", route.getCode());
        assertEquals("Bogotá - Tunja", route.getName());
        assertEquals("Bogotá", route.getOrigin());
        assertEquals("Tunja", route.getDestination());
        assertEquals(150, route.getDistanceKm());
        assertEquals(180, route.getDurationMin());
        assertNull(route.getId());
    }

    @Test
    @DisplayName("Debe actualizar la entidad Route")
    void updateEntity() {

        Route existingRoute = Route.builder()
                .id(1L)
                .code("BOG-TUN")
                .name("Old Name")
                .origin("Bogotá")
                .destination("Tunja")
                .distanceKm(150)
                .durationMin(180)
                .build();

        RouteUpdateRequest request = new RouteUpdateRequest(
                "Bogotá - Tunja Express",
                160,
                170
        );

        routeMapper.updateEntity(request, existingRoute);

        assertEquals("Bogotá - Tunja Express", existingRoute.getName());
        assertEquals(160, existingRoute.getDistanceKm());
        assertEquals(170, existingRoute.getDurationMin());

        assertEquals("BOG-TUN", existingRoute.getCode());
        assertEquals("Bogotá", existingRoute.getOrigin());
        assertEquals("Tunja", existingRoute.getDestination());
    }

    @Test
    @DisplayName("Debe mapear la entidad Route a RouteResponse")
    void toResponse() {

        Route route = Route.builder()
                .id(1L)
                .code("BOG-TUN")
                .name("Bogotá - Tunja")
                .origin("Bogotá")
                .destination("Tunja")
                .distanceKm(150)
                .durationMin(180)
                .stops(new ArrayList<>())
                .build();

        RouteResponse response = routeMapper.toResponse(route);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("BOG-TUN", response.code());
        assertEquals("Bogotá - Tunja", response.name());
        assertEquals("Bogotá", response.origin());
        assertEquals("Tunja", response.destination());
        assertEquals(150, response.distanceKm());
        assertEquals(180, response.durationMin());
        assertNotNull(response.stops());
        assertTrue(response.stops().isEmpty());
    }

    @Test
    @DisplayName("Debe mapear Route con paradas a RouteResponse con StopSummary")
    void mapStopsToSummary() {

        Route route = Route.builder()
                .id(1L)
                .code("BOG-TUN")
                .name("Bogotá - Tunja")
                .origin("Bogotá")
                .destination("Tunja")
                .distanceKm(150)
                .durationMin(180)
                .build();

        List<Stop> stops = new ArrayList<>();
        stops.add(Stop.builder()
                .id(1L)
                .name("Terminal Bogotá")
                .order(0)
                .lat(new BigDecimal("4.6533"))
                .lng(new BigDecimal("-74.0836"))
                .route(route)
                .build());
        stops.add(Stop.builder()
                .id(2L)
                .name("Zipaquirá")
                .order(1)
                .lat(new BigDecimal("5.0208"))
                .lng(new BigDecimal("-73.9949"))
                .route(route)
                .build());
        stops.add(Stop.builder()
                .id(3L)
                .name("Terminal Tunja")
                .order(2)
                .lat(new BigDecimal("5.5353"))
                .lng(new BigDecimal("-73.3678"))
                .route(route)
                .build());

        route.setStops(stops);

        RouteResponse response = routeMapper.toResponse(route);

        assertNotNull(response);
        assertNotNull(response.stops());
        assertEquals(3, response.stops().size());

        StopSummary firstStop = response.stops().get(0);
        assertEquals(1L, firstStop.id());
        assertEquals("Terminal Bogotá", firstStop.name());
        assertEquals(0, firstStop.order());
        assertEquals(new BigDecimal("4.6533"), firstStop.lat());
        assertEquals(new BigDecimal("-74.0836"), firstStop.lng());

        StopSummary secondStop = response.stops().get(1);
        assertEquals(2L, secondStop.id());
        assertEquals("Zipaquirá", secondStop.name());
        assertEquals(1, secondStop.order());

        StopSummary thirdStop = response.stops().get(2);
        assertEquals(3L, thirdStop.id());
        assertEquals("Terminal Tunja", thirdStop.name());
        assertEquals(2, thirdStop.order());
    }

    @Test
    @DisplayName("Debe manejar la lista de paradas nula (null stops list)")
    void shouldHandleNullStopsList() {

        Route route = Route.builder()
                .id(1L)
                .code("BOG-TUN")
                .name("Bogotá - Tunja")
                .origin("Bogotá")
                .destination("Tunja")
                .distanceKm(150)
                .durationMin(180)
                .stops(null)
                .build();

        RouteResponse response = routeMapper.toResponse(route);

        assertNotNull(response);
        assertNull(response.stops());
    }

    @Test
    @DisplayName("Debe preservar todos los campos durante el mapeo")
    void shouldPreserveAllFieldsDuringMapping() {
        RouteCreateRequest request = new RouteCreateRequest(
                "MED-CAL",
                "Medellín - Cali",
                "Medellín",
                "Cali",
                420,
                540
        );

        Route route = routeMapper.toEntity(request);
        route.setId(5L);
        RouteResponse response = routeMapper.toResponse(route);

        assertEquals(5L, response.id());
        assertEquals(request.code(), response.code());
        assertEquals(request.name(), response.name());
        assertEquals(request.origin(), response.origin());
        assertEquals(request.destination(), response.destination());
        assertEquals(request.distanceKm(), response.distanceKm());
        assertEquals(request.durationMin(), response.durationMin());
    }

    @Test
    @DisplayName("Debe manejar valores mínimos")
    void shouldHandleMinimumValues() {

        RouteCreateRequest request = new RouteCreateRequest(
                "A",
                "B",
                "C",
                "D",
                1,
                1
        );

        Route route = routeMapper.toEntity(request);

        assertNotNull(route);
        assertEquals("A", route.getCode());
        assertEquals(1, route.getDistanceKm());
        assertEquals(1, route.getDurationMin());
    }
}