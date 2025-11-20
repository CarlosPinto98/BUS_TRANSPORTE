package com.unimag.controllers;

import com.unimag.DTO.ParcelDTO;
import com.unimag.service.ParcelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/parcels")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ParcelController {

    private final ParcelService parcelService;


    @GetMapping("/all")
    public ResponseEntity<Page<ParcelDTO.parcelResponse>> getAll(Pageable pageable) {
        log.debug("Retrieving all parcels");

        Page<ParcelDTO.parcelResponse> parcels = parcelService.getAll(pageable);
        return ResponseEntity.ok(parcels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParcelDTO.parcelResponse> get(@PathVariable Long id) {
        log.debug("Retrieving parcel: {}", id);

        ParcelDTO.parcelResponse parcel = parcelService.get(id);
        return ResponseEntity.ok(parcel);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ParcelDTO.parcelResponse> get(@PathVariable String code) {
        log.debug("Retrieving parcel by code: {}", code);

        ParcelDTO.parcelResponse parcel = parcelService.get(code);
        return ResponseEntity.ok(parcel);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('CLERK', 'ADMIN')")
    public ResponseEntity<ParcelDTO.parcelResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ParcelDTO.parcelUpdateRequest request
    ) {
        log.info("Updating parcel: {}", id);

        ParcelDTO.parcelResponse updated = parcelService.update(request,id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.warn("Deleting parcel: {}", id);

        parcelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
