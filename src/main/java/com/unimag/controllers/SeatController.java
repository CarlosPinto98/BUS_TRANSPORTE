package com.unimag.controllers;

import com.unimag.DTO.RouteDTO;
import com.unimag.DTO.SeatDTO;
import com.unimag.service.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
@Validated
@Slf4j

public class SeatController {

    private final SeatService seatService;

    @PostMapping("/create")
    public ResponseEntity<RouteDTO.routeResponse> create(@Valid @RequestBody SeatDTO.seatCreateRequest request) {
        log.info("Creating new seat {} for bus: {}", request.number(), request.busId());

        RouteDTO.routeResponse created = seatService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<SeatDTO.seatResponse>> getAll(Pageable pageable) {
        log.debug("Retrieving all seats");

        Page<SeatDTO.seatResponse> seats = seatService.getAll(pageable);
        return ResponseEntity.ok(seats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatDTO.seatResponse> get(@PathVariable Long id) {
        log.debug("Retrieving seat: {}", id);

        SeatDTO.seatResponse seat = seatService.get(id);
        return ResponseEntity.ok(seat);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<SeatDTO.seatResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SeatDTO.seatUpdateRequest request
    ) {
        log.info("Updating seat: {}", id);

        SeatDTO.seatResponse updated = seatService.update(request, id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.warn("Deleting seat: {}", id);

        seatService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/bus/{busId}/number/{number}")
    public ResponseEntity<SeatDTO.seatResponse> getSeatByNumberAndBusId(
            @PathVariable int number,
            @PathVariable Long busId

    ) {
        log.debug("Retrieving seat {} for bus: {}", number, busId);

        SeatDTO.seatResponse seat = seatService.getSeatByNumberAndBusId(number,busId);
        return ResponseEntity.ok(seat);
    }
}
