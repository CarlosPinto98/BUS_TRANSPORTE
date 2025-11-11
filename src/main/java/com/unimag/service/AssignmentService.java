package com.unimag.service;


import com.unimag.DTO.AssignmentDTO;
import com.unimag.entities.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssignmentService {

    AssignmentDTO.assignmentResponse save(AssignmentDTO.assignmentCreateRequest assignmentDTO);
    AssignmentDTO.assignmentResponse get(Long id);
    Page<AssignmentDTO.assignmentResponse> getAll(Pageable pageable);
    void delete(Long id);
    AssignmentDTO.assignmentResponse update(Long id, AssignmentDTO.assignmentUpdateRequest request);
    Assignment getObject(Long id);
}
