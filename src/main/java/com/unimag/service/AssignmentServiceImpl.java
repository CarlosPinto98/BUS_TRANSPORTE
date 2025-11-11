package com.unimag.service;

import com.unimag.DTO.AssignmentDTO;
import com.unimag.entities.Assignment;
import com.unimag.entities.Enums.Role;
import com.unimag.mappers.AssignmentMapper;
import com.unimag.repository.AssignmentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Transactional

public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentMapper assignmentMapper;
    private final UserServiceImpl userService;
    private final TripServiceImpl tripService;

    @Override
    public AssignmentDTO.assignmentResponse save(AssignmentDTO.assignmentCreateRequest createRequest) {
        Assignment assignment = assignmentMapper.toEntity(createRequest);
        assignment.setDispatcher(userService.getObject(createRequest.dispatcherId(), Role.DISPATCHER));
        assignment.setDriver(userService.getObject(createRequest.driverId(), Role.DRIVER));
        assignment.setTrip(tripService.getObject(createRequest.tripId()));
        return assignmentMapper.toResponse(assignmentRepository.save(assignment));
    }

    @Override
    public AssignmentDTO.assignmentResponse get(Long id) {
        return assignmentMapper.toResponse(getObject(id));
    }

    @Override
    public Page<AssignmentDTO.assignmentResponse> getAll(Pageable pageable) {
        return assignmentRepository.findAll(pageable).map(assignmentMapper::toResponse);
    }

    @Override
    public void delete(Long id) {
        assignmentRepository.deleteById(id);
    }

    @Override
    public AssignmentDTO.assignmentResponse update(Long id, AssignmentDTO.assignmentUpdateRequest request) {
        var assignment = getObject(id);
        assignmentMapper.updateEntity(request, assignment);
        if (request.driverId() != null){
            assignment.setDriver(userService.getObject(request.driverId(), Role.DRIVER));
        }
        if (request.tripId() != null){
            assignment.setTrip(tripService.getObject(request.tripId()));
        }
        if (request.dispatcherId() != null){
            assignment.setDispatcher(userService.getObject(request.dispatcherId(), Role.DISPATCHER));
        }
        return assignmentMapper.toResponse(assignment);
    }

    @Override
    public Assignment getObject(Long id) {
        return assignmentRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("assignment not found"));
    }
}
