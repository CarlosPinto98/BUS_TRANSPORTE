package com.unimag.service;

import com.unimag.DTO.BusDTO.*;
import com.unimag.entities.Bus;
import com.unimag.entities.Enums.StatusBus;
import com.unimag.entities.Seat;
import com.unimag.mappers.BusMapper;
import com.unimag.repository.BusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusServiceImplTest {

    @Mock
    private BusRepository busRepository;
    @Spy
    private BusMapper busMapper = Mappers.getMapper(BusMapper.class);
    @InjectMocks

    private BusServiceImpl busService;
    private Bus bus;
    private busCreateRequest createRequest;
    private busUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        Map<String, Object> amenities = Map.of("wifi", true, "airConditioning", true);

        bus = Bus.builder()
                .id(1L)
                .plate("ABC-123")
                .capacity(40)
                .amenities(new HashMap<>(amenities))
                .statusBus(StatusBus.ACTIVE)
                .seats(new ArrayList<>())
                .build();

        createRequest = new busCreateRequest("ABC-123", 40, amenities, StatusBus.ACTIVE);
        updateRequest = new busUpdateRequest(50, amenities, StatusBus.MAINTENANCE);
    }

    @Test
    @DisplayName("Debe crear un bus exitosamente")
    void createBus() {

        when(busRepository.existsByPlate("ABC-123")).thenReturn(false);
        when(busRepository.save(any(Bus.class))).thenReturn(bus);

        busResponse response = busService.createBus(createRequest);

        assertNotNull(response);
        assertEquals("ABC-123", response.plate());
        verify(busRepository).save(any(Bus.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el placa ya existe")
    void ThrowExceptionWhenPlateExists() {
        when(busRepository.existsByPlate("ABC-123")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> busService.createBus(createRequest));
        verify(busRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe actualizar un bus exitosamente")
    void updateBus() {

        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(busRepository.save(any(Bus.class))).thenReturn(bus);

        busResponse response = busService.updateBus(1L, updateRequest);

        assertNotNull(response);
        verify(busMapper).updateEntity(updateRequest, bus);
        verify(busRepository).save(bus);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el bus no existe al actualizar")
    void ThrowExceptionOnUpdateWhenNotFound() {
        when(busRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> busService.updateBus(999L, updateRequest));
        verify(busRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe obtener un bus por id")
    void getBusById() {

        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));

        busResponse response = busService.getBusById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        verify(busRepository).findById(1L);
    }

    @Test
    @DisplayName("Debe obtener un bus con seats")
    void getBusWithSeats() {

        Seat seat = Seat.builder().id(1L).number("A1").build();
        bus.setSeats(List.of(seat));
        when(busRepository.findByIdWithSeats(1L)).thenReturn(Optional.of(bus));

        busResponse response = busService.getBusWithSeats(1L);

        assertNotNull(response);
        verify(busRepository).findByIdWithSeats(1L);
    }

    @Test
    @DisplayName("Debe obtener un bus por plate")
    void getBusbyPlate() {

        when(busRepository.findByPlate("ABC-123")).thenReturn(Optional.of(bus));

        busResponse response = busService.getBusbyPlate("ABC-123");

        assertNotNull(response);
        assertEquals("ABC-123", response.plate());
        verify(busRepository).findByPlate("ABC-123");
    }

    @Test
    @DisplayName("Debe obtener todos los buses")
    void getAllBuses() {

        Bus bus2 = Bus.builder().id(2L).plate("XYZ-789").capacity(50)
                .statusBus(StatusBus.ACTIVE).build();
        when(busRepository.findAll()).thenReturn(List.of(bus, bus2));

        List<busResponse> responses = busService.getAllBuses();

        assertEquals(2, responses.size());
        verify(busRepository).findAll();
    }

    @Test
    @DisplayName("Debe obtener buses por status")
    void getBusesByStatus() {

        when(busRepository.findByStatusBus(StatusBus.ACTIVE)).thenReturn(List.of(bus));

        List<busResponse> responses = busService.getBusesByStatus(StatusBus.ACTIVE);

        assertEquals(1, responses.size());
        verify(busRepository).findByStatusBus(StatusBus.ACTIVE);
    }

    @Test
    @DisplayName("Debe obtener buses disponibles por capacidad")
    void getAvailableBuses() {

        when(busRepository.findAvailableBusesByCapacity(StatusBus.ACTIVE, 30))
                .thenReturn(List.of(bus));

        List<busResponse> responses = busService.getAvailableBuses(30);

        assertEquals(1, responses.size());
        verify(busRepository).findAvailableBusesByCapacity(StatusBus.ACTIVE, 30);
    }

    @Test
    @DisplayName("Debe eliminar un bus")
    void deleteBus() {

        when(busRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> busService.deleteBus(1L));
        verify(busRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar bus inexistente")
    void ThrowExceptionOnDeleteWhenNotFound() {
        when(busRepository.existsById(999L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> busService.deleteBus(999L));
        verify(busRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debe verificar si existe un plate")
    void existsByPlate() {

        when(busRepository.existsByPlate("ABC-123")).thenReturn(true);
        boolean exists = busService.existsByPlate("ABC-123");
        assertTrue(exists);
        verify(busRepository).existsByPlate("ABC-123");
    }

    @Test
    @DisplayName("Debe cambiar el status del bus")
    void changeBusStatus() {

        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(busRepository.save(any(Bus.class))).thenReturn(bus);

        busResponse response = busService.changeBusStatus(1L, StatusBus.MAINTENANCE);

        assertNotNull(response);
        verify(busRepository).save(bus);
    }

    @Test
    @DisplayName("Debe lanzar excepción al cambiar status de bus inexistente")
    void ThrowExceptionOnChangeStatusWhenNotFound() {
        when(busRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> busService.changeBusStatus(999L, StatusBus.INACTIVE));
        verify(busRepository, never()).save(any());
    }
}