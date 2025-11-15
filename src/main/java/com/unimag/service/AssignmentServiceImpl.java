package com.unimag.service;

import com.unimag.DTO.AssignmentDTO;
import com.unimag.entities.Assignment;
import com.unimag.entities.Enums.Role;
import com.unimag.entities.Trip;
import com.unimag.entities.User;
import com.unimag.mappers.AssignmentMapper;
import com.unimag.repository.AssignmentRepository;
import com.unimag.repository.TripRepository;
import com.unimag.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final AssignmentMapper assignmentMapper;
    private final UserServiceImpl userService;
    private final TripServiceImpl tripService;
    private final TripRepository tripRepository;

    @Override
    public AssignmentDTO.assignmentResponse create(AssignmentDTO.assignmentCreateRequest request) {

        Trip trip = tripRepository.findById(request.tripId())
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + request.tripId()));

        User driver = userRepository.findById(request.driverId())
                .orElseThrow(() -> new IllegalArgumentException("Driver not found: " + request.driverId()));

        User dispatcher = userRepository.findById(request.dispatcherId())
                .orElseThrow(() -> new IllegalArgumentException("Dispatcher not found: " + request.dispatcherId()));

        // Validar roles
        if (driver.getRole() != Role.DRIVER) {
            throw new IllegalArgumentException("User " + driver.getId() + " is not a DRIVER");
        }

        if (dispatcher.getRole() != Role.DISPATCHER) {
            throw new IllegalArgumentException("User " + dispatcher.getId() + " is not a DISPATCHER");
        }

        if (assignmentRepository.existsByTripId(request.tripId())) {
            throw new IllegalArgumentException("Trip already has an assignment");
        }

        Assignment assignment = assignmentMapper.toEntity(request);
        assignment.setTrip(trip);
        assignment.setDriver(driver);
        assignment.setDispatcher(dispatcher);

        Assignment savedAssignment = assignmentRepository.save(assignment);
        return assignmentMapper.toResponse(savedAssignment);
    }

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
