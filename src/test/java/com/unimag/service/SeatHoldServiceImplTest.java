package com.unimag.service;

import com.unimag.DTO.SeatHoldDTO;
import com.unimag.entities.*;
import com.unimag.entities.Enums.Role;
import com.unimag.entities.Enums.StatusSeatHold;
import com.unimag.entities.Enums.StatusTrip;
import com.unimag.entities.Enums.Type;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.SeatHoldMapper;
import com.unimag.repository.SeatHoldRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatHoldServiceImplTest {

    @Mock
    private SeatHoldRepository seatHoldRepository;

    @Mock
    private SeatHoldMapper seatHoldMapper;

    @Mock
    private SeatService seatService;

    @Mock
    private TripService tripService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SeatHoldServiceImpl seatHoldService;

    private SeatHold seatHold;
    private Trip trip;
    private User user;
    private Seat seat;
    private Bus bus;
    private Route route;
    private SeatHoldDTO.seatHoldCreateRequest createRequest;
    private SeatHoldDTO.seatHoldUpdateRequest updateRequest;
    private SeatHoldDTO.seatHoldResponse response;

    @BeforeEach
    void setUp() {

        route = Route.builder()
                .id(1L)
                .code("RT-001")
                .name("Ruta Santa Marta - Barranquilla")
                .origin("Santa Marta")
                .destination("Barranquilla")
                .distanceKm(100)
                .durationMin(120)
                .build();

        bus = Bus.builder()
                .id(1L)
                .plate("ABC123")
                .capacity(40)
                .build();

        trip = Trip.builder()
                .id(1L)
                .date(LocalDate.now())
                .departureAt(LocalDateTime.now().plusHours(2))
                .arrivalEta(LocalDateTime.now().plusHours(4))
                .route(route)
                .bus(bus)
                .statusTrip(StatusTrip.SCHEDULED)
                .seatHolds(new HashSet<>())
                .build();

        user = User.builder()
                .id(1L)
                .name("Juan Perez")
                .email("juan@example.com")
                .phone("3001234567")
                .role(Role.PASSENGER)
                .seatHolds(new HashSet<>())
                .build();

        seat = Seat.builder()
                .id(1L)
                .number("A1")
                .type(Type.STANDARD)
                .bus(bus)
                .seatHolds(new HashSet<>())
                .build();

        seatHold = SeatHold.builder()
                .id(1L)
                .trip(trip)
                .seatNumber("A1")
                .user(user)
                .seat(seat)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .createdAt(LocalDateTime.now())
                .statusSeatHold(StatusSeatHold.HOLD)
                .build();

        createRequest = new SeatHoldDTO.seatHoldCreateRequest(
                1L,    // tripId
                "A1",  // seatNumber
                1L,    // fromStopId
                2L     // toStopId
        );

        updateRequest = new SeatHoldDTO.seatHoldUpdateRequest(
                StatusSeatHold.CONVERTED, // statusSeatHold
                1L,                        // userId
                1L,                        // tripId
                1L                         // seatId
        );

        response = new SeatHoldDTO.seatHoldResponse(
                1L,
                "A1",
                LocalDateTime.now().plusMinutes(15),
                "HOLD",
                LocalDateTime.now(),
                1L,
                1L,
                LocalDate.now().toString(),
                "10:00",
                "Ruta Santa Marta - Barranquilla",
                15
        );
    }

    @Test
    @DisplayName("Save - Debe guardar una reserva de asiento (método no implementado)")
    void save() {

        SeatHoldDTO.seatHoldResponse result = seatHoldService.save(createRequest);
        assertNull(result, "El método save() no está implementado y debe retornar null");
        verifyNoInteractions(seatHoldMapper, seatHoldRepository);
    }

    @Test
    @DisplayName("Get - Debe retornar null (método no implementado)")
    void get() {
        SeatHoldDTO.seatHoldResponse result = seatHoldService.get(1L);
        assertNull(result, "El método get() no está implementado y debe retornar null");
        verifyNoInteractions(seatHoldMapper, seatHoldRepository);
    }

    @Test
    @DisplayName("GetAll - Debe retornar null (método no implementado)")
    void getAll(Pageable pageable) {
        pageable = PageRequest.of(0, 10);
        Page<SeatHoldDTO.seatHoldResponse> result = (Page<SeatHoldDTO.seatHoldResponse>) seatHoldService.getAll(pageable);
        assertNull(result, "El método getAll() no está implementado y debe retornar null");
        verifyNoInteractions(seatHoldMapper, seatHoldRepository);
    }

    @Test
    @DisplayName("Delete - No debe hacer nada (método vacío)")
    void delete() {
        assertDoesNotThrow(() -> seatHoldService.delete(1L));
        verifyNoInteractions(seatHoldRepository);
    }

    @Test
    @DisplayName("Update - Debe actualizar una reserva exitosamente")
    void update() {

        when(seatHoldRepository.findById(1L)).thenReturn(Optional.of(seatHold));
        when(userService.getObject(1L)).thenReturn(user);
        when(tripService.getObject(1L)).thenReturn(trip);
        when(seatService.getObject(1L)).thenReturn(seat);
        doNothing().when(seatHoldMapper).updateEntity(updateRequest, seatHold);
        when(seatHoldRepository.save(any(SeatHold.class))).thenReturn(seatHold);
        when(seatHoldMapper.toResponse(seatHold)).thenReturn(response);

        SeatHoldDTO.seatHoldResponse result = seatHoldService.update(updateRequest, 1L);

        assertNotNull(result);
        assertEquals(response.id(), result.id());
        assertEquals(response.seatNumber(), result.seatNumber());

        verify(seatHoldRepository).findById(1L);
        verify(seatHoldMapper).updateEntity(updateRequest, seatHold);
        verify(userService).getObject(1L);
        verify(tripService).getObject(1L);
        verify(seatService).getObject(1L);
        verify(seatHoldRepository).save(seatHold);
        verify(seatHoldMapper).toResponse(seatHold);
    }

    @Test
    void getObject() {
    }

    @Test
    void isSeatfree() {
    }


    @Test
    @DisplayName("Update - Debe actualizar solo el status sin otros cambios")
    void updateOnlyStatus_WhenNoOtherChanges() {
        // Arrange
        SeatHoldDTO.seatHoldUpdateRequest statusOnlyUpdate =
                new SeatHoldDTO.seatHoldUpdateRequest(
                        StatusSeatHold.EXPIRED,
                        null,
                        null,
                        null
                );

        when(seatHoldRepository.findById(1L)).thenReturn(Optional.of(seatHold));
        doNothing().when(seatHoldMapper).updateEntity(statusOnlyUpdate, seatHold);
        when(seatHoldRepository.save(any(SeatHold.class))).thenReturn(seatHold);
        when(seatHoldMapper.toResponse(seatHold)).thenReturn(response);

        SeatHoldDTO.seatHoldResponse result = seatHoldService.update(statusOnlyUpdate, 1L);

        assertNotNull(result);

        verify(seatHoldRepository).findById(1L);
        verify(seatHoldMapper).updateEntity(statusOnlyUpdate, seatHold);
        verify(userService, never()).getObject(anyLong());
        verify(tripService, never()).getObject(anyLong());
        verify(seatService, never()).getObject(anyLong());
        verify(seatHoldRepository).save(seatHold);
    }

    @Test
    @DisplayName("Update - Debe actualizar solo el usuario")
    void updateOnlyUser_WhenOnlyUserIdChanges() {

        SeatHoldDTO.seatHoldUpdateRequest userOnlyUpdate =
                new SeatHoldDTO.seatHoldUpdateRequest(
                        StatusSeatHold.HOLD,
                        2L,
                        null,
                        null
                );

        User newUser = User.builder()
                .id(2L)
                .name("Maria Lopez")
                .seatHolds(new HashSet<>())
                .build();

        when(seatHoldRepository.findById(1L)).thenReturn(Optional.of(seatHold));
        when(userService.getObject(2L)).thenReturn(newUser);
        doNothing().when(seatHoldMapper).updateEntity(userOnlyUpdate, seatHold);
        when(seatHoldRepository.save(any(SeatHold.class))).thenReturn(seatHold);
        when(seatHoldMapper.toResponse(seatHold)).thenReturn(response);

        SeatHoldDTO.seatHoldResponse result = seatHoldService.update(userOnlyUpdate, 1L);

        assertNotNull(result);

        verify(userService).getObject(2L);
        verify(tripService, never()).getObject(anyLong());
        verify(seatService, never()).getObject(anyLong());
    }

    @Test
    @DisplayName("Update - Debe actualizar solo el trip")
    void updateOnlyTrip_WhenOnlyTripIdChanges() {

        SeatHoldDTO.seatHoldUpdateRequest tripOnlyUpdate =
                new SeatHoldDTO.seatHoldUpdateRequest(
                        StatusSeatHold.HOLD,
                        null,
                        2L,
                        null
                );

        Trip newTrip = Trip.builder()
                .id(2L)
                .date(LocalDate.now().plusDays(1))
                .seatHolds(new HashSet<>())
                .build();

        when(seatHoldRepository.findById(1L)).thenReturn(Optional.of(seatHold));
        when(tripService.getObject(2L)).thenReturn(newTrip);
        doNothing().when(seatHoldMapper).updateEntity(tripOnlyUpdate, seatHold);
        when(seatHoldRepository.save(any(SeatHold.class))).thenReturn(seatHold);
        when(seatHoldMapper.toResponse(seatHold)).thenReturn(response);

        SeatHoldDTO.seatHoldResponse result = seatHoldService.update(tripOnlyUpdate, 1L);

        assertNotNull(result);

        verify(tripService).getObject(2L);
        verify(userService, never()).getObject(anyLong());
        verify(seatService, never()).getObject(anyLong());
    }

    @Test
    @DisplayName("Update - Debe actualizar solo el seat")
    void updateUpdateOnlySeat_WhenOnlySeatIdChanges() {

        SeatHoldDTO.seatHoldUpdateRequest seatOnlyUpdate =
                new SeatHoldDTO.seatHoldUpdateRequest(
                        StatusSeatHold.HOLD,
                        null,
                        null,
                        2L
                );

        Seat newSeat = Seat.builder()
                .id(2L)
                .number("B2")
                .seatHolds(new HashSet<>())
                .build();

        when(seatHoldRepository.findById(1L)).thenReturn(Optional.of(seatHold));
        when(seatService.getObject(2L)).thenReturn(newSeat);
        doNothing().when(seatHoldMapper).updateEntity(seatOnlyUpdate, seatHold);
        when(seatHoldRepository.save(any(SeatHold.class))).thenReturn(seatHold);
        when(seatHoldMapper.toResponse(seatHold)).thenReturn(response);

        SeatHoldDTO.seatHoldResponse result = seatHoldService.update(seatOnlyUpdate, 1L);

        assertNotNull(result);

        verify(seatService).getObject(2L);
        verify(userService, never()).getObject(anyLong());
        verify(tripService, never()).getObject(anyLong());
    }

    @Test
    @DisplayName("Update - Debe cambiar status a CONVERTED")
    void updateChangeStatusToConverted() {

        SeatHoldDTO.seatHoldUpdateRequest convertedUpdate =
                new SeatHoldDTO.seatHoldUpdateRequest(
                        StatusSeatHold.CONVERTED,
                        null,
                        null,
                        null
                );

        when(seatHoldRepository.findById(1L)).thenReturn(Optional.of(seatHold));
        doAnswer(invocation -> {
            SeatHold sh = invocation.getArgument(1);
            sh.setStatusSeatHold(StatusSeatHold.CONVERTED);
            return null;
        }).when(seatHoldMapper).updateEntity(convertedUpdate, seatHold);

        when(seatHoldRepository.save(any(SeatHold.class))).thenReturn(seatHold);

        SeatHoldDTO.seatHoldResponse convertedResponse =
                new SeatHoldDTO.seatHoldResponse(
                        1L, "A1", LocalDateTime.now().plusMinutes(15),
                        "CONVERTED", LocalDateTime.now(), 1L, 1L,
                        LocalDate.now().toString(), "10:00",
                        "Ruta Santa Marta - Barranquilla", 15
                );
        when(seatHoldMapper.toResponse(seatHold)).thenReturn(convertedResponse);

        SeatHoldDTO.seatHoldResponse result = seatHoldService.update(convertedUpdate, 1L);

        assertNotNull(result);
        assertEquals("CONVERTED", result.statusSeatHold());
    }

    @Test
    @DisplayName("Update - Debe cambiar status a EXPIRED")
    void updateChangeStatusToExpired() {

        SeatHoldDTO.seatHoldUpdateRequest expiredUpdate =
                new SeatHoldDTO.seatHoldUpdateRequest(
                        StatusSeatHold.EXPIRED,
                        null,
                        null,
                        null
                );

        when(seatHoldRepository.findById(1L)).thenReturn(Optional.of(seatHold));
        doAnswer(invocation -> {
            SeatHold sh = invocation.getArgument(1);
            sh.setStatusSeatHold(StatusSeatHold.EXPIRED);
            return null;
        }).when(seatHoldMapper).updateEntity(expiredUpdate, seatHold);

        when(seatHoldRepository.save(any(SeatHold.class))).thenReturn(seatHold);

        SeatHoldDTO.seatHoldResponse expiredResponse =
                new SeatHoldDTO.seatHoldResponse(
                        1L, "A1", LocalDateTime.now().minusMinutes(5),
                        "EXPIRED", LocalDateTime.now(), 1L, 1L,
                        LocalDate.now().toString(), "10:00",
                        "Ruta Santa Marta - Barranquilla", 0
                );
        when(seatHoldMapper.toResponse(seatHold)).thenReturn(expiredResponse);

        SeatHoldDTO.seatHoldResponse result = seatHoldService.update(expiredUpdate, 1L);

        assertNotNull(result);
        assertEquals("EXPIRED", result.statusSeatHold());
    }

    @Test
    @DisplayName("Update - Debe lanzar excepción cuando la reserva no existe")
    void updateThrowException_WhenSeatHoldNotFound() {

        when(seatHoldRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> seatHoldService.update(updateRequest, 999L)
        );

        assertEquals("SeatHold not found", exception.getMessage());
        verify(seatHoldRepository).findById(999L);
        verify(seatHoldMapper, never()).updateEntity(any(), any());
    }

    @Test
    @DisplayName("GetObject - Debe retornar entidad SeatHold cuando existe")
    void getObjectReturnSeatHold_WhenExists() {

        when(seatHoldRepository.findById(1L)).thenReturn(Optional.of(seatHold));


        SeatHold result = seatHoldService.getObject(1L);


        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("A1", result.getSeatNumber());
        assertEquals(StatusSeatHold.HOLD, result.getStatusSeatHold());
        assertNotNull(result.getUser());
        assertEquals(1L, result.getUser().getId());
        assertNotNull(result.getTrip());
        assertEquals(1L, result.getTrip().getId());

        verify(seatHoldRepository).findById(1L);
    }

    @Test
    @DisplayName("GetObject - Debe lanzar excepción cuando no existe")
    void getObjectThrowException_WhenNotExists() {

        when(seatHoldRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> seatHoldService.getObject(999L)
        );

        assertEquals("SeatHold not found", exception.getMessage());
        verify(seatHoldRepository).findById(999L);
    }

    @Test
    @DisplayName("IsSeatFree - Debe retornar true cuando el asiento está libre")
    void isSeatfreeReturnTrue_WhenSeatIsFree() {

        when(seatHoldRepository.findByTripIdAndStatusSeatHold(1L, StatusSeatHold.HOLD))
                .thenReturn(List.of());

        boolean result = seatHoldService.isSeatfree(1L, 1L);

        assertTrue(result);
        verify(seatHoldRepository).findByTripIdAndStatusSeatHold(1L, StatusSeatHold.HOLD);
    }

    @Test
    @DisplayName("IsSeatFree - Debe lanzar excepción cuando el asiento está ocupado")
    void isSeatfreeThrowException_WhenSeatIsNotFree() {

        when(seatHoldRepository.findByTripIdAndStatusSeatHold(1L, StatusSeatHold.HOLD))
                .thenReturn(List.of(seatHold));


        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> seatHoldService.isSeatfree(1L, 1L)
        );

        assertEquals("Seat is not free", exception.getMessage());
        verify(seatHoldRepository).findByTripIdAndStatusSeatHold(1L, StatusSeatHold.HOLD);
    }

    @Test
    @DisplayName("IsSeatFree - Debe retornar true cuando hay reservas EXPIRED pero no HOLD")
    void isSeatfreeReturnTrue_WhenOnlyExpiredHoldsExist() {

        SeatHold expiredHold = SeatHold.builder()
                .id(2L)
                .seatNumber("A1")
                .statusSeatHold(StatusSeatHold.EXPIRED)
                .build();

        when(seatHoldRepository.findByTripIdAndStatusSeatHold(1L, StatusSeatHold.HOLD))
                .thenReturn(List.of());

        boolean result = seatHoldService.isSeatfree(1L, 1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("Update - Debe usar relaciones bidireccionales al actualizar User")
    void updateUseBidirectionalRelationship_WhenUpdatingUser() {

        User newUser = User.builder()
                .id(2L)
                .name("Carlos Rodriguez")
                .seatHolds(new HashSet<>())
                .build();

        SeatHoldDTO.seatHoldUpdateRequest userUpdate =
                new SeatHoldDTO.seatHoldUpdateRequest(
                        StatusSeatHold.HOLD, 2L, null, null
                );

        when(seatHoldRepository.findById(1L)).thenReturn(Optional.of(seatHold));
        when(userService.getObject(2L)).thenReturn(newUser);
        doNothing().when(seatHoldMapper).updateEntity(userUpdate, seatHold);
        when(seatHoldRepository.save(any(SeatHold.class))).thenReturn(seatHold);
        when(seatHoldMapper.toResponse(seatHold)).thenReturn(response);

        seatHoldService.update(userUpdate, 1L);

        verify(userService).getObject(2L);
    }

    @Test
    @DisplayName("Update - Debe usar relaciones bidireccionales al actualizar Seat")
    void updateUseBidirectionalRelationship_WhenUpdatingSeat() {

        Seat newSeat = Seat.builder()
                .id(2L)
                .number("C3")
                .seatHolds(new HashSet<>())
                .build();

        SeatHoldDTO.seatHoldUpdateRequest seatUpdate =
                new SeatHoldDTO.seatHoldUpdateRequest(
                        StatusSeatHold.HOLD, null, null, 2L
                );

        when(seatHoldRepository.findById(1L)).thenReturn(Optional.of(seatHold));
        when(seatService.getObject(2L)).thenReturn(newSeat);
        doNothing().when(seatHoldMapper).updateEntity(seatUpdate, seatHold);
        when(seatHoldRepository.save(any(SeatHold.class))).thenReturn(seatHold);
        when(seatHoldMapper.toResponse(seatHold)).thenReturn(response);

        seatHoldService.update(seatUpdate, 1L);

        verify(seatService).getObject(2L);
    }

}