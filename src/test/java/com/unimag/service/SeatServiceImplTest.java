package com.unimag.service;

import com.unimag.DTO.SeatDTO;
import com.unimag.entities.Bus;
import com.unimag.entities.Enums.Type;
import com.unimag.entities.Seat;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.SeatMapper;
import com.unimag.repository.SeatRepository;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatServiceImplTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private SeatMapper seatMapper;

    @Mock
    private BusService busService;

    @InjectMocks
    private SeatServiceImpl seatService;

    private Seat seat;
    private Bus bus;
    private SeatDTO.seatCreateRequest createRequest;
    private SeatDTO.seatUpdateRequest updateRequest;
    private SeatDTO.seatResponse response;

    @BeforeEach
    void setUp() {
        bus = Bus.builder()
                .id(1L)
                .plate("ABC123")
                .plate("ABC123")
                .capacity(40)
                .seats(new ArrayList<>())
                .build();

        // Setup Seat
        seat = Seat.builder()
                .id(1L)
                .number("A1")
                .type(Type.STANDARD)
                .bus(bus)
                .seatHolds(new HashSet<>())
                .build();

        // Setup DTOs
        createRequest = new SeatDTO.seatCreateRequest(
                "A1",           // number
                Type.STANDARD,  // type
                1L              // busId
        );

        updateRequest = new SeatDTO.seatUpdateRequest(
                Type.PREFERENTIAL, // type
                1L,                // busId
                2                  // number (nuevo número)
        );

        response = new SeatDTO.seatResponse(
                1L,
                "A1",
                "STANDARD",
                1L,
                "ABC123",
                40
        );
    }


    @Test
    @DisplayName("Save - Debe retornar null (método no implementado)")
    void save() {

        SeatDTO.seatResponse result = seatService.save(createRequest);
        assertNull(result, "El método save() no está implementado y debe retornar null");
        verifyNoInteractions(seatMapper, seatRepository);
    }

    @Test
    @DisplayName("Update - Debe actualizar un asiento exitosamente")
    void update() {

        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(busService.getObject(1L)).thenReturn(bus);
        when(seatRepository.findByNumberAndBusId("2", 1L)).thenReturn(Optional.empty());
        doNothing().when(seatMapper).updateEntity(updateRequest, seat);
        when(seatMapper.toResponse(seat)).thenReturn(response);

        SeatDTO.seatResponse result = seatService.update(updateRequest, 1L);

        assertNotNull(result);
        assertEquals(response.id(), result.id());

        verify(seatRepository).findById(1L);
        verify(seatMapper).updateEntity(updateRequest, seat);
        verify(busService).getObject(1L);
        verify(seatRepository).findByNumberAndBusId("2", 1L);
        verify(seatMapper).toResponse(seat);
    }

    @Test
    @DisplayName("Delete - Debe eliminar un asiento")
    void delete() {

        doNothing().when(seatRepository).deleteById(1L);
        assertDoesNotThrow(() -> seatService.delete(1L));
        verify(seatRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Get - Debe obtener un asiento por ID")
    void get() {

        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(seatMapper.toResponse(seat)).thenReturn(response);

        SeatDTO.seatResponse result = seatService.get(1L);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.id());
        assertEquals("A1", result.number());
        assertEquals("STANDARD", result.type());
        assertEquals(Long.valueOf(1L), result.busId());

        verify(seatRepository).findById(1L);
        verify(seatMapper).toResponse(seat);
    }

    @Test
    @DisplayName("GetAll - Debe retornar página de asientos")
    void getAll() {

        Pageable pageable = PageRequest.of(0, 10);
        List<Seat> seats = List.of(seat);
        Page<Seat> seatPage = new PageImpl<>(seats, pageable, 1);

        when(seatRepository.findAll(pageable)).thenReturn(seatPage);
        when(seatMapper.toResponse(seat)).thenReturn(response);

        Page<SeatDTO.seatResponse> result = seatService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(response.id(), result.getContent().get(0).id());

        verify(seatRepository).findAll(pageable);
        verify(seatMapper).toResponse(seat);
    }

    @Test
    @DisplayName("GetAll - Debe retornar página vacía cuando no hay asientos")
    void getAll_ShouldReturnEmptyPage_WhenNoSeats() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Seat> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(seatRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<SeatDTO.seatResponse> result = seatService.getAll(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(seatRepository).findAll(pageable);
        verify(seatMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("GetSeatByNumberAndBusId - Debe obtener asiento por número y busId")
    void getSeatByNumberAndBusId() {

        when(seatRepository.findByBusIdAndNumber(1L, "1"))
                .thenReturn(Optional.of(seat));

        SeatDTO.seatResponse result = seatService.getSeatByNumberAndBusId(1, 1L);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.id());
        assertEquals("A1", result.number());
        assertEquals(Type.STANDARD, result.type());

        verify(seatRepository).findByBusIdAndNumber(1L, "1");
    }

    @Test
    @DisplayName("GetObject - Debe retornar entidad Seat cuando existe")
    void getObject() {

        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        Seat result = seatService.getObject(1L);
        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
        assertEquals("A1", result.getNumber());
        assertEquals(Type.STANDARD, result.getType());
        assertNotNull(result.getBus());
        assertEquals(Long.valueOf(1L), result.getBus().getId());

        verify(seatRepository).findById(1L);
    }

    @Test
    @DisplayName("GetObject - Debe lanzar excepción cuando no existe")
    void getObject_ShouldThrowException_WhenNotExists() {

        when(seatRepository.findById(999L)).thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> seatService.getObject(999L)
        );

        assertEquals("Seat not found", exception.getMessage());
        verify(seatRepository).findById(999L);
    }

    @Test
    @DisplayName("GetSeatByNumberAndBusId - Debe lanzar excepción cuando no existe")
    void getSeatByNumberAndBusId_ShouldThrowException_WhenNotFound() {

        when(seatRepository.findByBusIdAndNumber(1L, "99"))
                .thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> seatService.getSeatByNumberAndBusId(99, 1L)
        );

        assertEquals("Seat not found", exception.getMessage());
        verify(seatRepository).findByBusIdAndNumber(1L, "99");
    }

    @Test
    @DisplayName("GetSeatByNumberAndBusId - Debe convertir número int a String")
    void getSeatByNumberAndBusId_ShouldConvertNumberToString() {

        when(seatRepository.findByBusIdAndNumber(1L, "15"))
                .thenReturn(Optional.of(seat));

        SeatDTO.seatResponse result = seatService.getSeatByNumberAndBusId(15, 1L);

        assertNotNull(result);
        verify(seatRepository).findByBusIdAndNumber(1L, "15");
    }

    @Test
    @DisplayName("Get - Debe lanzar excepción cuando el ID no existe")
    void get_ShouldThrowException_WhenIdNotFound() {

        when(seatRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> seatService.get(999L)
        );

        assertEquals("Seat not found", exception.getMessage());
        verify(seatRepository).findById(999L);
        verify(seatMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Update - Debe usar relación bidireccional al actualizar Bus")
    void update_ShouldUseBidirectionalRelationship_WhenUpdatingBus() {

        Bus newBus = Bus.builder()
                .id(2L)
                .plate("XYZ789")
                .capacity(50)
                .seats(new ArrayList<>())
                .build();

        SeatDTO.seatUpdateRequest busUpdate =
                new SeatDTO.seatUpdateRequest(Type.STANDARD, 2L, null);

        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(busService.getObject(2L)).thenReturn(newBus);
        doNothing().when(seatMapper).updateEntity(busUpdate, seat);
        when(seatMapper.toResponse(seat)).thenReturn(response);

        seatService.update(busUpdate, 1L);

        verify(busService).getObject(2L);
    }

    @Test
    @DisplayName("GetAll - Debe retornar asientos de diferentes tipos")
    void getAll_ShouldReturnSeatsOfDifferentTypes() {
        Seat standardSeat = Seat.builder()
                .id(1L)
                .number("A1")
                .type(Type.STANDARD)
                .bus(bus)
                .build();

        Seat preferentialSeat = Seat.builder()
                .id(2L)
                .number("P1")
                .type(Type.PREFERENTIAL)
                .bus(bus)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Seat> seatPage = new PageImpl<>(
                List.of(standardSeat, preferentialSeat), pageable, 2
        );

        SeatDTO.seatResponse standardResponse =
                new SeatDTO.seatResponse(1L, "A1", "STANDARD", 1L, "ABC123", 40);
        SeatDTO.seatResponse preferentialResponse =
                new SeatDTO.seatResponse(2L, "P1", "PREFERENTIAL", 1L, "ABC123", 40);

        when(seatRepository.findAll(pageable)).thenReturn(seatPage);
        when(seatMapper.toResponse(standardSeat)).thenReturn(standardResponse);
        when(seatMapper.toResponse(preferentialSeat)).thenReturn(preferentialResponse);

        Page<SeatDTO.seatResponse> result = seatService.getAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("STANDARD", result.getContent().get(0).type());
        assertEquals("PREFERENTIAL", result.getContent().get(1).type());
    }

    @Test
    @DisplayName("Update - Debe mantener el número actual cuando no se proporciona uno nuevo")
    void update_ShouldKeepCurrentNumber_WhenNoNewNumberProvided() {
        String originalNumber = seat.getNumber();

        SeatDTO.seatUpdateRequest updateWithoutNumber =
                new SeatDTO.seatUpdateRequest(Type.PREFERENTIAL, 1L, null);

        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(busService.getObject(1L)).thenReturn(bus);
        doNothing().when(seatMapper).updateEntity(updateWithoutNumber, seat);
        when(seatMapper.toResponse(seat)).thenReturn(response);

        SeatDTO.seatResponse result = seatService.update(updateWithoutNumber, 1L);

        assertNotNull(result);
        verify(seatRepository, never()).findByNumberAndBusId(any(), any());
        assertEquals(originalNumber, seat.getNumber());
    }
}