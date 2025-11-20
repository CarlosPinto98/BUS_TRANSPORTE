package com.unimag.controllers;

import com.unimag.DTO.StopDTO;
import com.unimag.service.StopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stops")
@RequiredArgsConstructor
@Slf4j

public class StopController {

    private final StopService stopService;

    @GetMapping("all")
    public ResponseEntity<Page
            <StopDTO.stopResponse>> getAll(Pageable pageable) {
        log.debug("Retrieving all stops");

        Page<StopDTO.stopResponse> stops = stopService.getAll(pageable);
        return ResponseEntity.ok(stops);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StopDTO.stopResponse> get(@PathVariable Long id) {
        log.debug("Retrieving stop: {}", id);

        StopDTO.stopResponse stop = stopService.get(id);
        return ResponseEntity.ok(stop);
    }



    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.warn("Deleting stop: {}", id);

        stopService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
