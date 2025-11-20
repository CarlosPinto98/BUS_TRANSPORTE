package com.unimag.controllers;

import com.unimag.DTO.AssignmentDTO;
import com.unimag.DTO.TripDTO;
import com.unimag.entities.Enums.StatusTrip;
import com.unimag.security.service.CustomUserDetails;
import com.unimag.service.AssignmentService;
import com.unimag.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
@Validated
@Slf4j
public class TripController {

    private final TripService tripService;
    private final AssignmentService assignmentService;

    @PostMapping("create")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TripDTO.tripResponse> createTrip(@Valid @RequestBody TripDTO.tripCreateRequest request) {
        log.info("Creating new trip for route: {}, date: {}", request.routeId(), request.date());
        TripDTO.tripResponse response = tripService.createTrip(request);
        log.info("Trip created successfully with ID: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<TripDTO.tripResponse> updateTrip(
            @PathVariable Long id,
            @Valid @RequestBody TripDTO.tripUpdateRequest request) {
        log.info("Updating trip ID: {}", id);
        TripDTO.tripResponse response = tripService.updateTrip(id, request);
        log.info("Trip ID: {} updated successfully", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/{id}")
    public ResponseEntity<TripDTO.tripResponse> getTripById(@PathVariable Long id) {
        log.debug("Getting trip by ID: {}", id);
        TripDTO.tripResponse response = tripService.getTripById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<TripDTO.tripResponse> getTripWithDetails(@PathVariable Long id) {
        log.debug("Getting trip details for ID: {}", id);
        TripDTO.tripResponse response = tripService.getTripWithDetails(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<TripDTO.tripResponse>> getAllTrips() {
        log.debug("Getting all trips");
        List<TripDTO.tripResponse> response = tripService.getAllTrips();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TripDTO.tripResponse>> searchTrips(
            @RequestParam(required = false) Long routeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) StatusTrip status) {
        log.debug("Searching trips - routeId: {}, date: {}, status: {}", routeId, date, status);
        List<TripDTO.tripResponse> response = tripService.searchTrips(routeId, date, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<TripDTO.tripResponse>> getTripsByRouteAndDate(
            @PathVariable Long routeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.debug("Getting trips for route: {} and date: {}", routeId, date);
        List<TripDTO.tripResponse> response = tripService.getTripsByRouteAndDate(routeId, date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bus/{busId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER', 'DRIVER')")
    public ResponseEntity<List<TripDTO.tripResponse>> getActiveTripsByBus(
            @PathVariable Long busId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.debug("Getting active trips for bus: {} on date: {}", busId, date);
        List<TripDTO.tripResponse> response = tripService.getActiveTripsByBus(busId, date);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DISPATCHER', 'DRIVER')")
    public ResponseEntity<TripDTO.tripResponse> changeTripStatus(
            @PathVariable Long id,
            @RequestParam StatusTrip status) {
        log.info("Changing trip ID: {} status to: {}", id, status);
        TripDTO.tripResponse response = tripService.changeTripStatus(id, status);
        log.info("Trip ID: {} status changed to: {}", id, status);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/boarding/open")
    @PreAuthorize("hasRole('DISPATCHER')")
    public ResponseEntity<TripDTO.tripResponse> openBoarding(@PathVariable Long id) {
        log.info("Opening boarding for trip ID: {}", id);
        TripDTO.tripResponse response = tripService.changeTripStatus(id, StatusTrip.BOARDING);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/boarding/close")
    @PreAuthorize("hasRole('DISPATCHER')")
    public ResponseEntity<TripDTO.tripResponse> closeBoarding(@PathVariable Long id) {
        log.info("Closing boarding for trip ID: {}", id);
        TripDTO.tripResponse response = tripService.changeTripStatus(id, StatusTrip.DEPARTED);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/depart")
    @PreAuthorize("hasAnyRole('DRIVER', 'DISPATCHER')")
    public ResponseEntity<TripDTO.tripResponse> markAsDeparted(@PathVariable Long id) {
        log.info("Marking trip ID: {} as departed", id);
        TripDTO.tripResponse response = tripService.changeTripStatus(id, StatusTrip.DEPARTED);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/arrive")
    @PreAuthorize("hasAnyRole('DRIVER', 'DISPATCHER')")
    public ResponseEntity<TripDTO.tripResponse> markAsArrived(@PathVariable Long id) {
        log.info("Marking trip ID: {} as arrived", id);
        TripDTO.tripResponse response = tripService.changeTripStatus(id, StatusTrip.ARRIVED);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('DISPATCHER', 'ADMIN')")
    public ResponseEntity<TripDTO.tripResponse> cancelTrip(@PathVariable Long id) {
        log.info("Canceling trip ID: {}", id);
        TripDTO.tripResponse response = tripService.changeTripStatus(id, StatusTrip.CANCELLED);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reactivate")
    @PreAuthorize("hasAnyRole('DISPATCHER', 'ADMIN')")
    public ResponseEntity<TripDTO.tripResponse> reactivateTrip(@PathVariable Long id) {
        log.info("Reactivating cancelled trip ID: {}", id);
        TripDTO.tripResponse response = tripService.changeTripStatus(id, StatusTrip.SCHEDULED);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        log.info("Deleting trip ID: {}", id);
        tripService.deleteTrip(id);
        log.info("Trip ID: {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/today")
    public ResponseEntity<List<TripDTO.tripResponse>> getTodayTrips() {
        log.debug("Getting today's trips");
        List<TripDTO.tripResponse> response = tripService.getTripsByRouteAndDate(null, LocalDate.now());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<TripDTO.tripResponse>> getTripsByStatus(@PathVariable StatusTrip status) {
        log.debug("Getting trips by status: {}", status);
        List<TripDTO.tripResponse> response = tripService.searchTrips(null, null, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/today/active")
    public ResponseEntity<List<TripDTO.tripResponse>> getTodayActiveTrips() {
        log.debug("Getting today's active trips");
        List<TripDTO.tripResponse> response = tripService.searchTrips(null, LocalDate.now(), null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/driver/my-trips")
    @PreAuthorize("hasAnyRole('DRIVER', 'DISPATCHER', 'ADMIN')")
    public ResponseEntity<List<AssignmentDTO.assignmentResponse>> getDriverTrips(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {

        log.debug("Getting driver trips for date: {}", date);

        try {

            Long driverId = getCurrentDriverId(authentication);

            List<AssignmentDTO.assignmentResponse> assignments = assignmentService.getAssignmentsByDriverAndDate(driverId, date);

            log.debug("Found {} assignments for driver {} on date {}",
                    assignments.size(), driverId, date);
            return ResponseEntity.ok(assignments);

        } catch (IllegalArgumentException e) {
            log.warn("Error getting driver trips: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error getting driver trips", e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/driver/current-trips")
    @PreAuthorize("hasAnyRole('DRIVER', 'DISPATCHER', 'ADMIN')")
    public ResponseEntity<List<AssignmentDTO.assignmentResponse>> getCurrentDriverTrips(Authentication authentication) {
        log.debug("Getting current driver trips");

        try {
            Long driverId = getCurrentDriverId(authentication);
            LocalDate today = LocalDate.now();

            List<AssignmentDTO.assignmentResponse> assignments = assignmentService.getAssignmentsByDriverAndDate(driverId,today);

            log.debug("Found {} current assignments for driver {}", assignments.size(), driverId);
            return ResponseEntity.ok(assignments);

        } catch (IllegalArgumentException e) {
            log.warn("Error getting current driver trips: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error getting current driver trips", e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/driver/active-trips")
    @PreAuthorize("hasAnyRole('DRIVER', 'DISPATCHER', 'ADMIN')")
    public ResponseEntity<List<AssignmentDTO.assignmentResponse>> getActiveDriverTrips(Authentication authentication) {
        log.debug("Getting active driver trips");

        try {
            Long driverId = getCurrentDriverId(authentication);

            List<AssignmentDTO.assignmentResponse> assignments = assignmentService.getActiveAssignmentsByDriver(driverId);

            log.debug("Found {} active assignments for driver {}", assignments.size(), driverId);
            return ResponseEntity.ok(assignments);

        } catch (IllegalArgumentException e) {
            log.warn("Error getting active driver trips: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error getting active driver trips", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private Long getCurrentDriverId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("User not authenticated");
        }

        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {

            if (!userDetails.hasRole("DRIVER")) {
                throw new IllegalArgumentException("User is not a driver");
            }
            return userDetails.getId();
        }

        throw new IllegalArgumentException("Unable to get driver ID from authentication");
    }
}
