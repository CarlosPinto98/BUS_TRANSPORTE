package com.unimag.controllers;

import com.unimag.DTO.AssignmentDTO.*;

import com.unimag.service.AssignmentService;
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
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AssignmentController {

    private final AssignmentService service;

//    @PostMapping("/create")
//    public ResponseEntity<AssignmentDTO.assignmentResponse> create(@Valid @RequestBody assignmentResponse req,
//                                                UriComponentsBuilder uriBuilder) {
//        var memberCreated = service.create(req);
//        var location = uriBuilder.path("/api/members/{id}").buildAndExpand(memberCreated.id()).toUri();
//        return ResponseEntity.created(location).body(memberCreated);
//    }

    @PostMapping("/create")
    public ResponseEntity<assignmentResponse > create(
            @Valid @RequestBody assignmentCreateRequest createRequest
    ) {
        log.info("Creating assignment for trip {} - driver: {}",
                createRequest.tripId(), createRequest.driverId());

        assignmentResponse created = service.create(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<assignmentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody assignmentUpdateRequest request
    ) {
        log.info("Updating assignment: {}", id);

        assignmentResponse updated = service.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<assignmentResponse>> getAll(Pageable pageable) {
        log.debug("Retrieving all assignments");

        Page<assignmentResponse> assignments = service.getAll(pageable);
        return ResponseEntity.ok(assignments);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.warn("Deleting assignment: {}", id);

        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/driver/{driverId}")
    public assignmentResponse get(@PathVariable Long driverId) {
        log.debug("Retrieving assignments for driver: {}", driverId);

        return (service.get(driverId));
    }
}
