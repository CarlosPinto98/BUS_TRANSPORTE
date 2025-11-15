package com.unimag.service;


import com.unimag.DTO.AssignmentDTO.*;
import com.unimag.entities.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssignmentService {

    assignmentResponse create(assignmentCreateRequest request);
    assignmentResponse save(assignmentCreateRequest assignmentDTO);
    assignmentResponse get(Long id);
    Page<assignmentResponse> getAll(Pageable pageable);
    void delete(Long id);
    assignmentResponse update(Long id, assignmentUpdateRequest request);
    Assignment getObject(Long id);
}
