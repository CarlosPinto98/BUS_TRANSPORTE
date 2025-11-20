package com.unimag.controllers;

import com.unimag.DTO.FareRuleDTO;
import com.unimag.service.FareRuleService;
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
@RequestMapping("/api/v1/fare-rules")
@RequiredArgsConstructor
@Validated
@Slf4j

public class FareRuleController {

    private final FareRuleService fareRuleService;

    @GetMapping("/all")
    public ResponseEntity<Page<FareRuleDTO.fareRuleResponse>> getAll(Pageable pageable) {
        log.debug("Retrieving all fare rules");

        Page<FareRuleDTO.fareRuleResponse> fareRules = fareRuleService.getAll(pageable);
        return ResponseEntity.ok(fareRules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FareRuleDTO.fareRuleResponse> get(@PathVariable Long id) {
        log.debug("Retrieving fare rule: {}", id);

        FareRuleDTO.fareRuleResponse fareRule = fareRuleService.get(id);
        return ResponseEntity.ok(fareRule);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.warn("Deleting fare rule: {}", id);

        fareRuleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('DISPATCHER', 'ADMIN')")
    public ResponseEntity<FareRuleDTO.fareRuleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody FareRuleDTO.fareRuleUpdateRequest updateRequest
    ) {
        log.info("Updating fare rule: {}", id);

        FareRuleDTO.fareRuleResponse updated = fareRuleService.update(updateRequest,id);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('DISPATCHER', 'ADMIN')")
    public ResponseEntity<FareRuleDTO.fareRuleResponse> createFareRule(@Valid @RequestBody FareRuleDTO.fareRuleCreateRequest request) {
        log.info("Creating fare rule for route: {}", request.routeId());

        FareRuleDTO.fareRuleResponse created = fareRuleService.createFareRule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
