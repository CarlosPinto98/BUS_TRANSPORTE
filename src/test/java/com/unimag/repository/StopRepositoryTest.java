package com.unimag.repository;

import com.unimag.AbstractRepositoryTest;
import com.unimag.entities.Route;
import com.unimag.entities.Stop;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StopRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private StopRepository stopRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Test
    @DisplayName("Buscar stop de una route y ordenar por secuencia")
    void findByRouteIdOrderByOrderAsc() {

        Route route = createRoute("ORDERED-ROUTE");
        Route savedRoute = routeRepository.save(route);

        Stop stop1 = createStop(savedRoute, "Stop 1", 1);
        Stop stop2 = createStop(savedRoute, "Stop 2", 2);
        Stop stop3 = createStop(savedRoute, "Stop 3", 3);

        stopRepository.saveAll(List.of(stop3, stop1, stop2)); // Save in random order

        List<Stop> stops = stopRepository.findByRouteIdOrderByOrderAsc(savedRoute.getId());

        assertThat(stops).hasSize(3);
        assertThat(stops.get(0).getName()).isEqualTo("Stop 1");
        assertThat(stops.get(1).getName()).isEqualTo("Stop 2");
        assertThat(stops.get(2).getName()).isEqualTo("Stop 3");
    }

    @Test
    @DisplayName("Buscar todas las stop de un id de route")
    void findByRouteId() {

        Route route1 = createRoute("ROUTE-1");
        Route route2 = createRoute("ROUTE-2");
        Route savedRoute1 = routeRepository.save(route1);
        Route savedRoute2 = routeRepository.save(route2);

        stopRepository.save(createStop(savedRoute1, "Route1 Stop1", 1));
        stopRepository.save(createStop(savedRoute1, "Route1 Stop2", 2));
        stopRepository.save(createStop(savedRoute2, "Route2 Stop1", 1));

        List<Stop> route1Stops = stopRepository.findByRouteId(savedRoute1.getId());

        assertThat(route1Stops).hasSize(2);
        assertThat(route1Stops).extracting(Stop::getName)
                .containsExactlyInAnyOrder("Route1 Stop1", "Route1 Stop2");
    }

    @Test
    @DisplayName("Buscar parada por nombre ignorando mayus")
    void findByNameContainingIgnoreCase() {

        Route route = createRoute("SEARCH-ROUTE");
        Route savedRoute = routeRepository.save(route);

        stopRepository.save(createStop(savedRoute, "Terminal Bogotá", 1));
        stopRepository.save(createStop(savedRoute, "Terminal Medellín", 2));
        stopRepository.save(createStop(savedRoute, "Parada Tunja", 3));

        List<Stop> terminals = stopRepository.findByNameContainingIgnoreCase("terminal");

        assertThat(terminals).hasSize(2);
        assertThat(terminals).extracting(Stop::getName)
                .containsExactlyInAnyOrder("Terminal Bogotá", "Terminal Medellín");
    }


    @Test
    @DisplayName("Guardar stop")
    void shouldSaveStop() {
        Route route = createRoute("TEST-ROUTE");
        Route savedRoute = routeRepository.save(route);

        var stop = createStop(savedRoute,"Bogotá Terminal",1);
        stop.setLat(new BigDecimal("4.7110"));
        stop.setLng(new BigDecimal("-74.0721"));

        Stop saved = stopRepository.save(stop);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Bogotá Terminal");
    }

    private Route createRoute(String code) {
        var route =  Route.builder()
                .code(code)
                .name("Test Route")
                .origin("Origin")
                .destination("Destination")
                .distanceKm(100)
                .durationMin(120)
                .build();
        return route;
    }

    private Stop createStop(Route route, String name, Integer order) {
        var stop =  Stop.builder()
                .route(route)
                .name(name)
                .order(order)
                .lat(new BigDecimal("4.0000"))
                .lng(new BigDecimal("-74.0000"))
                .build();
        return stop;
    }

}