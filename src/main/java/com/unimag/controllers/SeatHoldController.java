package com.unimag.controllers;

import com.unimag.DTO.SeatHoldDTO.*;
import com.unimag.security.service.CustomUserDetails;
import com.unimag.service.SeatHoldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seat-holds")
@RequiredArgsConstructor
@Validated
@Slf4j

public class SeatHoldController {

    private final SeatHoldService seatHoldService;

    // ==================== ENDPOINTS AUTENTICADOS ====================

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('PASSENGER', 'CLERK', 'ADMIN')")
    public ResponseEntity<seatHoldResponse> create(
            Authentication authentication,
            @Valid @RequestBody seatHoldCreateRequest request
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        log.info("User {} creating seat hold for trip {} seat {}",
                userId, request.tripId(), request.seatNumber());

        seatHoldResponse created = seatHoldService.create(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/my-holds")
    @PreAuthorize("hasAnyRole('PASSENGER', 'CLERK', 'ADMIN')")
    public ResponseEntity<List<seatHoldResponse>> getMyHolds(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        log.debug("User {} retrieving their seat holds", userId);

        List<seatHoldResponse> holds = seatHoldService.getSeatHoldsByUserId(userId);
        return ResponseEntity.ok(holds);
    }

    @DeleteMapping("/{id}/release")
    @PreAuthorize("hasAnyRole('PASSENGER', 'CLERK', 'ADMIN')")
    public ResponseEntity<Void> releaseSeatHold(@PathVariable Long id) {
        log.info("Releasing seat hold: {}", id);

        seatHoldService.releaseSeatHold(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== CLERK/ADMIN ENDPOINTS ====================

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('CLERK', 'ADMIN')")
    public ResponseEntity<List<seatHoldResponse>> getAll(Pageable pageable) {
        log.debug("Retrieving all seat holds");

        List<seatHoldResponse> holds = seatHoldService.getAll(pageable);
        return ResponseEntity.ok(holds);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLERK', 'ADMIN')")
    public ResponseEntity<seatHoldResponse> getSeatHoldById(@PathVariable Long id) {
        log.debug("Retrieving seat hold: {}", id);

        seatHoldResponse hold = seatHoldService.getSeatHoldById(id);
        return ResponseEntity.ok(hold);
    }

    @GetMapping("/trip/{tripId}")
    @PreAuthorize("hasAnyRole('CLERK', 'DRIVER', 'DISPATCHER', 'ADMIN')")
    public ResponseEntity<List<seatHoldResponse>> getSeatHoldsByTrip(@PathVariable Long tripId) {
        log.debug("Retrieving seat holds for trip: {}", tripId);

        List<seatHoldResponse> holds = seatHoldService.getSeatHoldsByTripId(tripId);
        return ResponseEntity.ok(holds);
    }

    @GetMapping("/trip/{tripId}/active")
    @PreAuthorize("hasAnyRole('CLERK', 'DRIVER', 'DISPATCHER', 'ADMIN')")
    public ResponseEntity<List<seatHoldResponse>> getActiveSeatHoldsByTrip(@PathVariable Long tripId) {
        log.debug("Retrieving active seat holds for trip: {}", tripId);

        List<seatHoldResponse> holds = seatHoldService.getActiveSeatHoldsByTrip(tripId);
        return ResponseEntity.ok(holds);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CLERK', 'ADMIN')")
    public ResponseEntity<List<seatHoldResponse>> getSeatHoldsByUser(@PathVariable Long userId) {
        log.debug("Retrieving seat holds for user: {}", userId);

        List<seatHoldResponse> holds = seatHoldService.getSeatHoldsByUserId(userId);
        return ResponseEntity.ok(holds);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('CLERK', 'ADMIN')")
    public ResponseEntity<seatHoldResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody seatHoldUpdateRequest request
    ) {
        log.info("Updating seat hold: {}", id);

        seatHoldResponse updated = seatHoldService.update(request, id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('CLERK', 'ADMIN')")
    public ResponseEntity<Void> deleteSeatHold(@PathVariable Long id) {
        log.warn("Deleting seat hold: {}", id);

        seatHoldService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/convert")
    @PreAuthorize("hasAnyRole('CLERK', 'ADMIN')")
    public ResponseEntity<Void> convertHoldToTicket(@PathVariable Long id) {
        log.info("Converting seat hold {} to ticket", id);

        seatHoldService.convertHoldToTicket(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check")
    @PreAuthorize("hasAnyRole('CLERK', 'DRIVER', 'DISPATCHER', 'ADMIN')")
    public ResponseEntity<Boolean> isSeatHeld(
            @RequestParam Long tripId,
            @RequestParam String seatNumber
    ) {
        log.debug("Checking if seat {} is held for trip: {}", seatNumber, tripId);

        boolean isHeld = seatHoldService.isSeatHeld(tripId, seatNumber);
        return ResponseEntity.ok(isHeld);
    }
}
