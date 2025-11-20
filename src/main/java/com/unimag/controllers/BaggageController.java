package com.unimag.controllers;

import com.unimag.DTO.BaggageDTO;
import com.unimag.service.BaggageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/baggage")
@RequiredArgsConstructor
@Validated
@Slf4j

public class BaggageController {

    private final BaggageService baggageService;

    @PostMapping("/create")
    public ResponseEntity<BaggageDTO.baggageResponse> create(@Valid @RequestBody BaggageDTO.baggageCreateRequest createRequest) {
        log.info("Creating baggage for ticket: {}", createRequest.ticketId());

        BaggageDTO.baggageResponse created = baggageService.create(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<BaggageDTO.baggageResponse>> getAll(PageRequest pageRequest) {
        log.debug("Retrieving all baggage");

        Page<BaggageDTO.baggageResponse> baggage = baggageService.getAll(pageRequest);
        return ResponseEntity.ok(baggage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaggageDTO.baggageResponse> get(@PathVariable Long id) {
        log.debug("Retrieving baggage: {}", id);

        BaggageDTO.baggageResponse baggage = baggageService.get(id);
        return ResponseEntity.ok(baggage);
    }

    @GetMapping("/tag/{tagCode}")
    public ResponseEntity<BaggageDTO.baggageResponse> get(@PathVariable String tagCode) {
        log.debug("Retrieving baggage by tag: {}", tagCode);

        BaggageDTO.baggageResponse baggage = baggageService.get(tagCode);
        return ResponseEntity.ok(baggage);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BaggageDTO.baggageResponse> update(
            @Valid @RequestBody BaggageDTO.baggageUpdateRequest updateRequest,
            @PathVariable Long id)
    {
        log.info("Updating baggage: {}", id);

        BaggageDTO.baggageResponse updated = baggageService.update(updateRequest,id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.warn("Deleting baggage: {}", id);

        baggageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/trip/{tripId}")
    @PreAuthorize("hasAnyRole('CLERK', 'DRIVER', 'DISPATCHER', 'ADMIN')")
    public ResponseEntity<List<BaggageDTO.baggageResponse>> getObject(@PathVariable Long tripId) {
        log.debug("Retrieving baggage for trip: {}", tripId);

        List<BaggageDTO.baggageResponse> baggage = (List<BaggageDTO.baggageResponse>) baggageService.getObject(tripId);
        return ResponseEntity.ok(baggage);
    }
}
