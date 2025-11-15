package com.unimag.service;

import com.unimag.DTO.AssignmentDTO;
import com.unimag.entities.*;
import com.unimag.entities.Enums.Role;
import com.unimag.entities.Enums.StatusTrip;
import com.unimag.mappers.AssignmentMapper;
import com.unimag.repository.AssignmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceImplTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private AssignmentMapper assignmentMapper;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private TripServiceImpl tripService;

    @InjectMocks
    private AssignmentServiceImpl assignmentService;

    private Assignment assignment;
    private User driver;
    private User dispatcher;
    private Trip trip;
    private Route route;
    private Bus bus;
    private AssignmentDTO.assignmentCreateRequest createRequest;
    private AssignmentDTO.assignmentUpdateRequest updateRequest;
    private AssignmentDTO.assignmentResponse response;

    @BeforeEach
    void setUp() {

        route = Route.builder()
                .id(1L)
                .origin("Santa Marta")
                .destination("Barranquilla")
                .build();

        bus = Bus.builder()
                .id(1L)
                .plate("ABC123")
                .build();

        trip = Trip.builder()
                .id(1L)
                .date(LocalDate.now())
                .departureAt(LocalDateTime.now().plusHours(2))
                .arrivalEta(LocalDateTime.now().plusHours(4))
                .route(route)
                .bus(bus)
                .statusTrip(StatusTrip.SCHEDULED)
                .build();

        driver = User.builder()
                .id(1L)
                .name("Juan Perez")
                .email("juan@example.com")
                .phone("3001234567")
                .role(Role.DRIVER)
                .build();

        dispatcher = User.builder()
                .id(2L)
                .name("Maria Lopez")
                .email("maria@example.com")
                .phone("3009876543")
                .role(Role.DISPATCHER)
                .build();

        assignment = Assignment.builder()
                .id(1L)
                .checklistOk(false)
                .assignedAt(LocalDateTime.now())
                .trip(trip)
                .driver(driver)
                .dispatcher(dispatcher)
                .build();


        createRequest = new AssignmentDTO.assignmentCreateRequest(
                1L, // tripId
                1L, // driverId
                2L  // dispatcherId
        );

        updateRequest = new AssignmentDTO.assignmentUpdateRequest(
                true,  // checklistOk
                1L,    // tripId
                1L,    // driverId
                2L     // dispatcherId
        );

        response = new AssignmentDTO.assignmentResponse(
                1L,
                false,
                LocalDateTime.now(),
                1L,
                "Santa Marta → Barranquilla - 2024-01-01 10:00",
                "SCHEDULED",
                1L,
                "Juan Perez",
                2L,
                "Maria Lopez"
        );
    }

    @Test
    @DisplayName("Save - Debe guardar una asignación exitosamente")
    void save_ShouldSaveAssignmentSuccessfully() {

        when(assignmentMapper.toEntity(createRequest)).thenReturn(assignment);
        when(userService.getObject(1L, Role.DRIVER)).thenReturn(driver);
        when(userService.getObject(2L, Role.DISPATCHER)).thenReturn(dispatcher);
        when(tripService.getObject(1L)).thenReturn(trip);
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(assignment);
        when(assignmentMapper.toResponse(assignment)).thenReturn(response);

        AssignmentDTO.assignmentResponse result = assignmentService.save(createRequest);

        assertNotNull(result);
        assertEquals(response.id(), result.id());
        assertEquals(response.driverName(), result.driverName());
        assertEquals(response.dispatcherName(), result.dispatcherName());

        verify(assignmentMapper).toEntity(createRequest);
        verify(userService).getObject(1L, Role.DRIVER);
        verify(userService).getObject(2L, Role.DISPATCHER);
        verify(tripService).getObject(1L);
        verify(assignmentRepository).save(assignment);
        verify(assignmentMapper).toResponse(assignment);
    }

    @Test
    @DisplayName("Save - Debe lanzar excepción cuando el driver no existe")
    void save_ShouldThrowException_WhenDriverNotFound() {
        when(assignmentMapper.toEntity(createRequest)).thenReturn(assignment);
        when(userService.getObject(2L, Role.DISPATCHER)).thenReturn(dispatcher);
        when(userService.getObject(1L, Role.DRIVER))
                .thenThrow(new EntityNotFoundException("user with that rol does not exist"));

        assertThrows(EntityNotFoundException.class, () -> assignmentService.save(createRequest));

        verify(assignmentMapper).toEntity(createRequest);
        verify(userService).getObject(2L, Role.DISPATCHER);
        verify(userService).getObject(1L, Role.DRIVER);
        verify(assignmentRepository, never()).save(any());
        verify(tripService, never()).getObject(anyLong());
    }

    @Test
    @DisplayName("Get - Debe obtener una asignación por ID")
    void get_ShouldReturnAssignment_WhenIdExists() {
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(assignmentMapper.toResponse(assignment)).thenReturn(response);

        AssignmentDTO.assignmentResponse result = assignmentService.get(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Juan Perez", result.driverName());

        verify(assignmentRepository).findById(1L);
        verify(assignmentMapper).toResponse(assignment);
    }

    @Test
    @DisplayName("Get - Debe lanzar excepción cuando el ID no existe")
    void get_ShouldThrowException_WhenIdNotFound() {

        when(assignmentRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> assignmentService.get(999L)
        );

        assertEquals("assignment not found", exception.getMessage());
        verify(assignmentRepository).findById(999L);
        verify(assignmentMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("GetAll - Debe retornar página de asignaciones")
    void getAll_ShouldReturnPageOfAssignments() {

        Pageable pageable = PageRequest.of(0, 10);
        List<Assignment> assignments = List.of(assignment);
        Page<Assignment> assignmentPage = new PageImpl<>(assignments, pageable, 1);

        when(assignmentRepository.findAll(pageable)).thenReturn(assignmentPage);
        when(assignmentMapper.toResponse(assignment)).thenReturn(response);

        Page<AssignmentDTO.assignmentResponse> result = assignmentService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(response.id(), result.getContent().get(0).id());

        verify(assignmentRepository).findAll(pageable);
        verify(assignmentMapper).toResponse(assignment);
    }

    @Test
    @DisplayName("GetAll - Debe retornar página vacía cuando no hay asignaciones")
    void getAll_ShouldReturnEmptyPage_WhenNoAssignments() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Assignment> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(assignmentRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<AssignmentDTO.assignmentResponse> result = assignmentService.getAll(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(assignmentRepository).findAll(pageable);
        verify(assignmentMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Delete - Debe eliminar una asignación por ID")
    void delete_ShouldDeleteAssignment() {

        doNothing().when(assignmentRepository).deleteById(1L);
        assertDoesNotThrow(() -> assignmentService.delete(1L));
        verify(assignmentRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Update - Debe actualizar una asignación exitosamente")
    void update_ShouldUpdateAssignmentSuccessfully() {
        // Arrange
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(userService.getObject(1L, Role.DRIVER)).thenReturn(driver);
        when(userService.getObject(2L, Role.DISPATCHER)).thenReturn(dispatcher);
        when(tripService.getObject(1L)).thenReturn(trip);
        doNothing().when(assignmentMapper).updateEntity(updateRequest, assignment);
        when(assignmentMapper.toResponse(assignment)).thenReturn(response);

        AssignmentDTO.assignmentResponse result = assignmentService.update(1L, updateRequest);

        assertNotNull(result);
        assertEquals(response.id(), result.id());

        verify(assignmentRepository).findById(1L);
        verify(assignmentMapper).updateEntity(updateRequest, assignment);
        verify(userService).getObject(1L, Role.DRIVER);
        verify(userService).getObject(2L, Role.DISPATCHER);
        verify(tripService).getObject(1L);
        verify(assignmentMapper).toResponse(assignment);
    }

    @Test
    @DisplayName("Update - Debe actualizar solo checklistOk cuando no hay otros cambios")
    void update_ShouldUpdateOnlyChecklistOk_WhenNoOtherChanges() {

        AssignmentDTO.assignmentUpdateRequest partialUpdate =
                new AssignmentDTO.assignmentUpdateRequest(true, null, null, null);

        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        doNothing().when(assignmentMapper).updateEntity(partialUpdate, assignment);
        when(assignmentMapper.toResponse(assignment)).thenReturn(response);


        AssignmentDTO.assignmentResponse result = assignmentService.update(1L, partialUpdate);

        assertNotNull(result);

        verify(assignmentRepository).findById(1L);
        verify(assignmentMapper).updateEntity(partialUpdate, assignment);
        verify(userService, never()).getObject(anyLong(), any(Role.class));
        verify(tripService, never()).getObject(anyLong());
        verify(assignmentMapper).toResponse(assignment);
    }

    @Test
    @DisplayName("Update - Debe lanzar excepción cuando la asignación no existe")
    void update_ShouldThrowException_WhenAssignmentNotFound() {
        when(assignmentRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> assignmentService.update(999L, updateRequest)
        );

        assertEquals("assignment not found", exception.getMessage());
        verify(assignmentRepository).findById(999L);
        verify(assignmentMapper, never()).updateEntity(any(), any());
    }

    @Test
    @DisplayName("GetObject - Debe retornar entidad Assignment cuando existe")
    void getObject_ShouldReturnAssignment_WhenExists() {

        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));

        Assignment result = assignmentService.getObject(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(driver.getId(), result.getDriver().getId());
        assertEquals(dispatcher.getId(), result.getDispatcher().getId());
        assertEquals(trip.getId(), result.getTrip().getId());

        verify(assignmentRepository).findById(1L);
    }

    @Test
    @DisplayName("GetObject - Debe lanzar excepción cuando no existe")
    void getObject_ShouldThrowException_WhenNotExists() {
        when(assignmentRepository.findById(999L)).thenReturn(Optional.empty());


        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> assignmentService.getObject(999L)
        );

        assertEquals("assignment not found", exception.getMessage());
        verify(assignmentRepository).findById(999L);
    }

    @Test
    @DisplayName("Update - Debe actualizar solo el driver cuando solo driverId cambia")
    void update_ShouldUpdateOnlyDriver_WhenOnlyDriverIdChanges() {
        AssignmentDTO.assignmentUpdateRequest driverOnlyUpdate =
                new AssignmentDTO.assignmentUpdateRequest(false, null, 3L, null);

        User newDriver = User.builder()
                .id(3L)
                .name("Carlos Rodriguez")
                .role(Role.DRIVER)
                .build();

        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(userService.getObject(3L, Role.DRIVER)).thenReturn(newDriver);
        doNothing().when(assignmentMapper).updateEntity(driverOnlyUpdate, assignment);
        when(assignmentMapper.toResponse(assignment)).thenReturn(response);

        AssignmentDTO.assignmentResponse result = assignmentService.update(1L, driverOnlyUpdate);

        assertNotNull(result);

        verify(assignmentRepository).findById(1L);
        verify(assignmentMapper).updateEntity(driverOnlyUpdate, assignment);
        verify(userService).getObject(3L, Role.DRIVER);
        verify(userService, never()).getObject(anyLong(), eq(Role.DISPATCHER));
        verify(tripService, never()).getObject(anyLong());
    }

    @Test
    @DisplayName("Save - Debe lanzar excepción cuando el trip no existe")
    void save_ShouldThrowException_WhenTripNotFound() {

        when(assignmentMapper.toEntity(createRequest)).thenReturn(assignment);
        when(userService.getObject(1L, Role.DRIVER)).thenReturn(driver);
        when(userService.getObject(2L, Role.DISPATCHER)).thenReturn(dispatcher);
        when(tripService.getObject(1L))
                .thenThrow(new EntityNotFoundException("Trip not found"));

        assertThrows(EntityNotFoundException.class, () -> assignmentService.save(createRequest));

        verify(tripService).getObject(1L);
        verify(assignmentRepository, never()).save(any());
    }
}