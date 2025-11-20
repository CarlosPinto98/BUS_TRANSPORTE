package com.unimag.controllers;

import com.unimag.DTO.RouteDTO;
import com.unimag.DTO.StopDTO;
import com.unimag.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
@Validated
@Slf4j

public class RouteController {

    private final RouteService routeService;

    @GetMapping("/all")
    public ResponseEntity<List<RouteDTO.routeResponse>> getAllRoutes() {
        log.debug("Retrieving all routes");

        List<RouteDTO.routeResponse> routes = routeService.getAllRoutes();
        return ResponseEntity.ok(routes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteDTO.routeResponse> getRouteById(@PathVariable Long id) {
        log.debug("Retrieving route: {}", id);

        RouteDTO.routeResponse route = routeService.getRouteById(id);
        return ResponseEntity.ok(route);
    }

    @GetMapping("/{id}/stops")
    public ResponseEntity<List<StopDTO.stopResponse>> getRouteStops(@PathVariable Long id) {
        log.debug("Retrieving stops for route: {}", id);

        List<StopDTO.stopResponse> stops = routeService.getStopsByRoute(id);
        return ResponseEntity.ok(stops);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RouteDTO.routeResponse>> searchRoutes(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination
    ) {
        log.debug("Searching routes - origin: {}, destination: {}", origin, destination);

        List<RouteDTO.routeResponse> routes = routeService.searchRoutes(origin, destination);
        return ResponseEntity.ok(routes);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<RouteDTO.routeResponse> getRouteByCode(@PathVariable String code) {
        log.debug("Retrieving route by code: {}", code);

        RouteDTO.routeResponse route = routeService.getRouteByCode(code);
        return ResponseEntity.ok(route);
    }

    @GetMapping("/{id}/with-stops")
    public ResponseEntity<RouteDTO.routeResponse> getRouteWithStops(@PathVariable Long id) {
        log.debug("Retrieving route with stops: {}", id);

        RouteDTO.routeResponse route = routeService.getRouteWithStops(id);
        return ResponseEntity.ok(route);
    }

    // ==================== DISPATCHER/ADMIN ENDPOINTS ====================

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('DISPATCHER', 'ADMIN')")
    public ResponseEntity<RouteDTO.routeResponse> createRoute(@Valid @RequestBody RouteDTO.routeCreateRequest request) {
        log.info("Creating new route: {}", request.code());

        RouteDTO.routeResponse created = routeService.createRoute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('DISPATCHER', 'ADMIN')")
    public ResponseEntity<RouteDTO.routeResponse> updateRoute(
            @PathVariable Long id,
            @Valid @RequestBody RouteDTO.routeUpdateRequest request
    ) {
        log.info("Updating route: {}", id);

        RouteDTO.routeResponse updated = routeService.updateRoute(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        log.warn("Deleting route: {}", id);

        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/code/{code}/exists")
    @PreAuthorize("hasAnyRole('DISPATCHER', 'ADMIN')")
    public ResponseEntity<Boolean> checkCodeExists(@PathVariable String code) {
        log.debug("Checking if route code exists: {}", code);

        boolean exists = routeService.existsByCode(code);
        return ResponseEntity.ok(exists);
    }
}
