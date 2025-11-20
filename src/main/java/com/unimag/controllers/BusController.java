package com.unimag.controllers;

import com.unimag.DTO.BusDTO;
import com.unimag.entities.Enums.StatusBus;
import com.unimag.service.BusService;
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
@RequestMapping("/api/v1/buses")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BusController {

    private final BusService busService;

    @PostMapping("/create")
    public ResponseEntity<BusDTO.busResponse> createBus(@Valid @RequestBody BusDTO.busCreateRequest createRequest) {
        log.info("Creating new bus with plate: {}", createRequest.plate());

        BusDTO.busResponse created = busService.createBus(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BusDTO.busResponse>> getAllBuses() {
        log.debug("Retrieving all buses");

        List<BusDTO.busResponse> buses = busService.getAllBuses();
        return ResponseEntity.ok(buses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusDTO.busResponse> getBusById(@PathVariable Long id) {
        log.debug("Retrieving bus: {}", id);

        BusDTO.busResponse bus = busService.getBusById(id);
        return ResponseEntity.ok(bus);
    }

    @GetMapping("/{id}/with-seats")
    public ResponseEntity<BusDTO.busResponse> getBusWithSeats(@PathVariable Long id) {
        log.debug("Retrieving bus with seats: {}", id);

        BusDTO.busResponse bus = busService.getBusWithSeats(id);
        return ResponseEntity.ok(bus);
    }

    @GetMapping("/plate/{plate}")
    public ResponseEntity<BusDTO.busResponse> getBusByPlate(@PathVariable String plate) {
        log.debug("Retrieving bus by plate: {}", plate);

        BusDTO.busResponse bus = busService.getBusbyPlate(plate);
        return ResponseEntity.ok(bus);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BusDTO.busResponse> updateBus(
            @PathVariable Long id,
            @Valid @RequestBody BusDTO.busUpdateRequest request
    ) {
        log.info("Updating bus: {}", id);

        BusDTO.busResponse updated = busService.updateBus(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBus(@PathVariable Long id) {
        log.warn("Deleting bus: {}", id);

        busService.deleteBus(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== FILTROS Y CONSULTAS ====================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BusDTO.busResponse>> getBusesByStatus(@PathVariable StatusBus status) {
        log.debug("Retrieving buses with status: {}", status);

        List<BusDTO.busResponse> buses = busService.getBusesByStatus(status);
        return ResponseEntity.ok(buses);
    }

    @GetMapping("/available")
    public ResponseEntity<List<BusDTO.busResponse>> getAvailableBuses(
            @RequestParam(required = false, defaultValue = "1") Integer minCapacity
    ) {
        log.debug("Retrieving available buses with min capacity: {}", minCapacity);

        List<BusDTO.busResponse> buses = busService.getAvailableBuses(minCapacity);
        return ResponseEntity.ok(buses);
    }

    @GetMapping("/plate/{plate}/exists")
    public ResponseEntity<Boolean> checkPlateExists(@PathVariable String plate) {
        log.debug("Checking if plate exists: {}", plate);

        boolean exists = busService.existsByPlate(plate);
        return ResponseEntity.ok(exists);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BusDTO.busResponse> changeBusStatus(
            @PathVariable Long id,
            @RequestParam StatusBus status
    ) {
        log.info("Changing status of bus {} to {}", id, status);

        BusDTO.busResponse updated = busService.changeBusStatus(id, status);
        return ResponseEntity.ok(updated);
    }

}
