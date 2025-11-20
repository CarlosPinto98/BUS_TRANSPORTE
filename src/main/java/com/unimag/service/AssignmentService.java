package com.unimag.service;


import com.unimag.DTO.AssignmentDTO.*;
import com.unimag.entities.Assignment;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AssignmentService {

    assignmentResponse create(@Valid assignmentResponse request);
    assignmentResponse save(assignmentCreateRequest assignmentDTO);
    assignmentResponse get(Long id);
    Page<assignmentResponse> getAll(Pageable pageable);
    void delete(Long id);
    assignmentResponse update(Long id, assignmentUpdateRequest request);
    List<assignmentResponse> getAssignmentsByDriverAndDate(Long driverId, LocalDate date);
    Assignment getObject(Long id);
    List<assignmentResponse> getActiveAssignmentsByDriver(Long driverId);
}
