package com.unimag.service;

import com.unimag.DTO.TripDTO.*;
import com.unimag.entities.*;
import com.unimag.entities.Enums.StatusTrip;
import com.unimag.entities.Enums.Type;
import com.unimag.mappers.TripMapper;
import com.unimag.repository.BusRepository;
import com.unimag.repository.RouteRepository;
import com.unimag.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripServiceImplTest {

    @Mock
    private TripRepository tripRepository;
    @Mock

    private RouteRepository routeRepository;
    @Mock

    private BusRepository busRepository;
    @Spy

    private TripMapper tripMapper = Mappers.getMapper(TripMapper.class);
    @InjectMocks

    private TripServiceImpl tripService;
    private Trip trip;
    private Route route;
    private Bus bus;
    private tripCreateRequest createRequest;
    private tripUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        route = Route.builder()
                .id(1L)
                .code("BOG-TUN")
                .name("Bogotá - Tunja")
                .build();

        bus = Bus.builder()
                .id(1L)
                .plate("ABC123")
                .capacity(40)
                .build();

        trip = Trip.builder()
                .id(1L)
                .date(LocalDate.of(2025, 12, 25))
                .departureAt(LocalDateTime.of(2025, 12, 25, 8, 0))
                .arrivalEta(LocalDateTime.of(2025, 12, 25, 11, 0))
                .statusTrip(StatusTrip.SCHEDULED)
                .route(route)
                .bus(bus)
                .build();

        createRequest = new tripCreateRequest(
                LocalDate.of(2025, 12, 25),
                LocalDateTime.of(2025, 12, 25, 8, 0),
                LocalDateTime.of(2025, 12, 25, 11, 0),
                1L,
                1L
        );

        updateRequest = new tripUpdateRequest(
                LocalDateTime.of(2025, 12, 25, 9, 0),
                LocalDateTime.of(2025, 12, 25, 12, 0),
                1L,
                StatusTrip.BOARDING
        );
    }

    @Test
    @DisplayName("Debe crear un trip")
    void createTrip() {

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(tripRepository.findActiveTripsByBusAndDate(any(), any()))
                .thenReturn(Collections.emptyList());
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        tripResponse result = tripService.createTrip(createRequest);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(routeRepository).findById(1L);
        verify(busRepository).findById(1L);
        verify(tripRepository).save(any(Trip.class));
        verify(tripMapper).toEntity(createRequest);
        verify(tripMapper).toResponse(any(Trip.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la route no existe")
    void ThrowExceptionWhenRouteNotFound() {
        when(routeRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tripService.createTrip(createRequest)
        );

        assertTrue(exception.getMessage().contains("Route not found"));
        verify(routeRepository).findById(1L);
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el bus no existe")
    void ThrowExceptionWhenBusNotFound() {
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(busRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tripService.createTrip(createRequest)
        );

        assertTrue(exception.getMessage().contains("Bus not found"));
        verify(busRepository).findById(1L);
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la hora de arrival es antes de departure")
    void shouldThrowExceptionWhenArrivalBeforeDeparture() {
        tripCreateRequest invalidRequest = new tripCreateRequest(
                LocalDate.of(2025, 12, 25),
                LocalDateTime.of(2025, 12, 25, 11, 0),
                LocalDateTime.of(2025, 12, 25, 8, 0),
                1L,
                1L
        );
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(tripRepository.findActiveTripsByBusAndDate(any(), any()))
                .thenReturn(Collections.emptyList());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tripService.createTrip(invalidRequest)
        );

        assertTrue(exception.getMessage().contains("Arrival time must be after departure time"));
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    @DisplayName("Debe actualizar un trip exitosamente")
    void updateTrip() {

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        tripResponse result = tripService.updateTrip(1L, updateRequest);

        assertNotNull(result);
        verify(tripRepository).findById(1L);
        verify(tripMapper).updateEntity(updateRequest, trip);
        verify(tripRepository).save(trip);
    }

    @Test
    @DisplayName("Debe obtener trip por ID")
    void getTripById() {

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        tripResponse result = tripService.getTripById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(tripRepository).findById(1L);
        verify(tripMapper).toResponse(trip);
    }

    @Test
    @DisplayName("Debe obtener trip con details")
    void getTripWithDetails() {

        when(tripRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(trip));
        tripResponse result = tripService.getTripWithDetails(1L);
        assertNotNull(result);
        verify(tripRepository).findByIdWithDetails(1L);
    }

    @Test
    void getAllTrips() {
    }

    @Test
    @DisplayName("Debe obtener trips por route y date")
    void getTripsByRouteAndDate() {

        List<Trip> trips = Arrays.asList(trip);
        when(routeRepository.existsById(1L)).thenReturn(true);
        when(tripRepository.findByRouteIdAndDate(1L, LocalDate.of(2025, 12, 25)))
                .thenReturn(trips);

        List<tripResponse> result = tripService.getTripsByRouteAndDate(
                1L, LocalDate.of(2025, 12, 25)
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(routeRepository).existsById(1L);
    }

    @Test
    void getTripsByRouteAndDateAndStatus() {
    }

    @Test
    void searchTrips() {
    }

    @Test
    void getTripsByDateAndStatus() {
    }

    @Test
    @DisplayName("Debe obtener trips activos por bus")
    void getActiveTripsByBus() {

        List<Trip> trips = Arrays.asList(trip);
        when(busRepository.existsById(1L)).thenReturn(true);
        when(tripRepository.findActiveTripsByBusAndDate(1L, LocalDate.of(2025, 12, 25)))
                .thenReturn(trips);

        List<tripResponse> result = tripService.getActiveTripsByBus(
                1L, LocalDate.of(2025, 12, 25)
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(busRepository).existsById(1L);
    }

    @Test
    @DisplayName("Debe eliminar un trip")
    void deleteTrip() {

        when(tripRepository.existsById(1L)).thenReturn(true);
        tripService.deleteTrip(1L);
        verify(tripRepository).existsById(1L);
        verify(tripRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debe cambiar el trip status")
    void changeTripStatus() {

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        tripResponse result = tripService.changeTripStatus(1L, StatusTrip.BOARDING);

        assertNotNull(result);
        verify(tripRepository).findById(1L);
        verify(tripRepository).save(trip);
        assertEquals(StatusTrip.BOARDING, trip.getStatusTrip());
    }

    @Test
    @DisplayName("Debe lanzar excepción al cambiar status cancelado")
    void shouldThrowExceptionWhenChangingCancelledStatus() {
        trip.setStatusTrip(StatusTrip.CANCELLED);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tripService.changeTripStatus(1L, StatusTrip.BOARDING)
        );

        assertTrue(exception.getMessage().contains("Cannot change status from"));
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción con transición de status inválida")
    void ThrowExceptionWithInvalidStatusTransition() {
        trip.setStatusTrip(StatusTrip.SCHEDULED);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tripService.changeTripStatus(1L, StatusTrip.ARRIVED)
        );

        assertTrue(exception.getMessage().contains("Invalid status transition"));
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    @DisplayName("Debe validar bus schedule sin conflicts")
    void validateTripSchedule() {

        when(tripRepository.findActiveTripsByBusAndDate(any(), any()))
                .thenReturn(Collections.emptyList());

        assertDoesNotThrow(() ->
                tripService.validateTripSchedule(
                        1L,
                        LocalDate.of(2025, 12, 25),
                        LocalDateTime.of(2025, 12, 25, 8, 0)
                )
        );
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando bus tiene schedule conflict")
    void ThrowExceptionWhenBusHasScheduleConflict() {
        Trip conflictingTrip = Trip.builder()
                .id(2L)
                .departureAt(LocalDateTime.of(2025, 12, 25, 7, 30))
                .arrivalEta(LocalDateTime.of(2025, 12, 25, 10, 30))
                .build();
        when(tripRepository.findActiveTripsByBusAndDate(any(), any()))
                .thenReturn(Arrays.asList(conflictingTrip));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tripService.validateTripSchedule(
                        1L,
                        LocalDate.of(2025, 12, 25),
                        LocalDateTime.of(2025, 12, 25, 8, 0)
                )
        );

        assertTrue(exception.getMessage().contains("already scheduled"));
    }

    @Test
    void getObject() {
//        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
//
//        Trip result = tripService.getObject(1L);
//
//        assertNotNull(result);
//        assertEquals(Long.valueOf(1L), result.getId());
//        assertEquals("A1", result.getId());
//        assertEquals(Type.STANDARD, result.getStatusTrip());
//        assertNotNull(result.getBus());
//        assertEquals(Long.valueOf(1L), result.getBus().getId());
//
//        verify(tripRepository).findById(1L);

    }
}