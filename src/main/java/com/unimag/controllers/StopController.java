package com.unimag.controllers;

import com.unimag.DTO.StopDTO.*;
import com.unimag.service.StopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stops")
@RequiredArgsConstructor
@Slf4j

public class StopController {

    private final StopService stopService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('DISPATCHER', 'ADMIN')")
    public ResponseEntity<stopResponse> create(@Valid @RequestBody stopCreateRequest request) {
        log.info("Creating new stop for route: {}", request.routeId());

        stopResponse created = stopService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("all")
    public ResponseEntity<Page
            <stopResponse>> getAll(Pageable pageable) {
        log.debug("Retrieving all stops");

        Page<stopResponse> stops = stopService.getAll(pageable);
        return ResponseEntity.ok(stops);
    }

    @GetMapping("/{id}")
    public ResponseEntity<stopResponse> get(@PathVariable Long id) {
        log.debug("Retrieving stop: {}", id);

        stopResponse stop = stopService.get(id);
        return ResponseEntity.ok(stop);
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.warn("Deleting stop: {}", id);

        stopService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("all")
    public ResponseEntity<List<stopResponse>> getAllStops() {
        log.debug("Retrieving all stops");

        List<stopResponse> stops = stopService.getAllStops();
        return ResponseEntity.ok(stops);
    }

    @GetMapping("/{id}")
    public ResponseEntity<stopResponse> getStopById(@PathVariable Long id) {
        log.debug("Retrieving stop: {}", id);

        stopResponse stop = stopService.getStopById(id);
        return ResponseEntity.ok(stop);
    }

    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<stopResponse>> getStopsByRoute(@PathVariable Long routeId) {
        log.debug("Retrieving stops for route: {}", routeId);

        List<stopResponse> stops = stopService.getStopsByRouteId(routeId);
        return ResponseEntity.ok(stops);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('DISPATCHER', 'ADMIN')")
    public ResponseEntity<stopResponse> updateStop(
            @PathVariable Long id,
            @Valid @RequestBody stopUpdateRequest request
    ) {
        log.info("Updating stop: {}", id);

        stopResponse updated = stopService.updateStop(id, request);
        return ResponseEntity.ok(updated);
    }
}
