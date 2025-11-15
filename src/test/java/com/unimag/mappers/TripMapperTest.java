package com.unimag.mappers;

import com.unimag.DTO.TicketDTO;
import com.unimag.DTO.TripDTO.*;
import com.unimag.entities.Bus;
import com.unimag.entities.Enums.PaymentMethod;
import com.unimag.entities.Enums.StatusBus;
import com.unimag.entities.Enums.StatusTicket;
import com.unimag.entities.Enums.StatusTrip;
import com.unimag.entities.Route;
import com.unimag.entities.Ticket;
import com.unimag.entities.Trip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("TripMapper Tests")
class TripMapperTest {

    private TripMapper tripMapper;

    @BeforeEach
    void setUp() {
        tripMapper = Mappers.getMapper(TripMapper.class);
    }

    @Test
    @DisplayName("Debe mapear tripCreateRequest a la entidad Trip")
    void toEntity() {

        LocalDate date = LocalDate.of(2025, 12, 25);
        LocalDateTime departure = LocalDateTime.of(2025, 12, 25, 8, 0);
        LocalDateTime arrival = LocalDateTime.of(2025, 12, 25, 11, 0);

        tripCreateRequest request = new tripCreateRequest(
                date,
                departure,
                arrival,
                1L,
                2L
        );

        Trip trip = tripMapper.toEntity(request);

        assertNotNull(trip);
        assertEquals(date, trip.getDate());
        assertEquals(departure, trip.getDepartureAt());
        assertEquals(arrival, trip.getArrivalEta());
        assertEquals(StatusTrip.SCHEDULED, trip.getStatusTrip());
        assertNotNull(trip.getRoute());
        assertEquals(1L, trip.getRoute().getId());
        assertNotNull(trip.getBus());
        assertEquals(2L, trip.getBus().getId());
        assertNull(trip.getId());
    }

    @Test
    @DisplayName("Debe actualizar la entidad Trip desde tripUpdateRequest")
    void updateEntity() {

        Ticket existingTicket = Ticket.builder()
                .id(1L)
                .seatNumber("1A")
                .price(new BigDecimal("50000"))
                .paymentMethod(PaymentMethod.CASH)
                .statusTicket(StatusTicket.SOLD)
                .qrCode("QR-123")
                .build();

        TicketDTO.ticketUpdateRequest request = new TicketDTO.ticketUpdateRequest(
                StatusTicket.CANCELLED
        );

         ticketMapper.updateEntity(request, existingTicket);

        assertEquals(StatusTicket.CANCELLED, existingTicket.getStatusTicket() );
        assertEquals("1A", existingTicket.getSeatNumber());
        assertEquals(new BigDecimal("50000"), existingTicket.getPrice());
    }

    @Test
    @DisplayName("Debe mapear la entidad Trip a tripResponse")
    void toResponse() {

        Route route = Route.builder()
                .id(1L)
                .code("BOG-TUN")
                .name("Bogotá - Tunja")
                .origin("Bogotá")
                .destination("Tunja")
                .distanceKm(150)
                .durationMin(180)
                .build();

        Bus bus = Bus.builder()
                .id(2L)
                .plate("ABC123")
                .capacity(40)
                .amenities(new HashMap<>())
                .statusBus(StatusBus.ACTIVE)
                .build();

        LocalDate date = LocalDate.of(2025, 12, 25);
        LocalDateTime departure = LocalDateTime.of(2025, 12, 25, 8, 0);
        LocalDateTime arrival = LocalDateTime.of(2025, 12, 25, 11, 0);

        Trip trip = Trip.builder()
                .id(1L)
                .date(date)
                .departureAt(departure)
                .arrivalEta(arrival)
                .statusTrip(StatusTrip.SCHEDULED)
                .route(route)
                .bus(bus)
                .build();

        tripResponse response = tripMapper.toResponse(trip);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(date, response.date());
        assertEquals(departure, response.departureAt());
        assertEquals(arrival, response.arrivalEta());
        assertEquals("SCHEDULED", response.statusTrip());
        assertEquals(1L, response.routeId());
        assertEquals("Bogotá - Tunja", response.routeName());
        assertEquals("Bogotá", response.origin());
        assertEquals("Tunja", response.destination());
        assertEquals(2L, response.busId());
        assertEquals("ABC123", response.busPlate());
        assertEquals(40, response.capacity());
    }


    @Test
    @DisplayName("Debe mapear tripCreateRequest sin asignación de bus")
    void mapBus() {

        LocalDate date = LocalDate.of(2025, 12, 25);
        LocalDateTime departure = LocalDateTime.of(2025, 12, 25, 8, 0);
        LocalDateTime arrival = LocalDateTime.of(2025, 12, 25, 11, 0);

        tripCreateRequest request = new tripCreateRequest(
                date,
                departure,
                arrival,
                1L,
                null
        );

        Trip trip = tripMapper.toEntity(request);

        assertNotNull(trip);
        assertNull(trip.getBus());
    }

    @Test
    @DisplayName("Debe mapear todos los tipos de TripStatus correctamente")
    void shouldMapAllTripStatusTypes() {
        for (StatusTrip status : StatusTrip.values()) {
            Trip trip = Trip.builder()
                    .id(1L)
                    .date(LocalDate.now())
                    .departureAt(LocalDateTime.now())
                    .arrivalEta(LocalDateTime.now().plusHours(3))
                    .statusTrip(status)
                    .route(Route.builder().id(1L).name("Test").build())
                    .bus(Bus.builder().id(1L).plate("TEST").capacity(40).build())
                    .build();

            tripResponse response = tripMapper.toResponse(trip);

            assertEquals(status.name(), response.statusTrip());
        }
    }

    @Test
    @DisplayName("Debe manejar viajes sin asignación de bus")
    void HandleTripWithoutBusAssignment() {
        Route route = Route.builder()
                .id(1L)
                .name("Test Route")
                .origin("A")
                .destination("B")
                .build();

        Trip trip = Trip.builder()
                .id(1L)
                .date(LocalDate.now())
                .departureAt(LocalDateTime.now())
                .arrivalEta(LocalDateTime.now().plusHours(2))
                .statusTrip(StatusTrip.SCHEDULED)
                .route(route)
                .bus(null)
                .build();

        tripResponse response = tripMapper.toResponse(trip);

        assertNotNull(response);
        assertNull(response.busId());
        assertNull(response.busPlate());
        assertNull(response.capacity());
    }

    @Test
    @DisplayName("Debe manejar diferentes combinaciones de fecha y hora")
    void HandleDifferentDateTimeCombinations() {
        LocalDate date1 = LocalDate.of(2025, 1, 1);
        LocalDateTime departure1 = LocalDateTime.of(2025, 1, 1, 0, 30);
        LocalDateTime arrival1 = LocalDateTime.of(2025, 1, 1, 3, 30);

        tripCreateRequest request1 = new tripCreateRequest(
                date1, departure1, arrival1, 1L, null
        );

        Trip trip1 = tripMapper.toEntity(request1);
        assertEquals(departure1, trip1.getDepartureAt());
        assertEquals(arrival1, trip1.getArrivalEta());

        LocalDate date2 = LocalDate.of(2025, 12, 31);
        LocalDateTime departure2 = LocalDateTime.of(2025, 12, 31, 23, 0);
        LocalDateTime arrival2 = LocalDateTime.of(2026, 1, 1, 2, 0);

        tripCreateRequest request2 = new tripCreateRequest(
                date2, departure2, arrival2, 1L, null
        );

        Trip trip2 = tripMapper.toEntity(request2);
        assertEquals(departure2, trip2.getDepartureAt());
        assertEquals(arrival2, trip2.getArrivalEta());
    }

    @Test
    @DisplayName("Debe preservar todos los campos durante el ciclo completo de mapeo")
    void PreserveAllFieldsDuringFullCycleMapping() {
        LocalDate date = LocalDate.of(2025, 6, 15);
        LocalDateTime departure = LocalDateTime.of(2025, 6, 15, 14, 30);
        LocalDateTime arrival = LocalDateTime.of(2025, 6, 15, 18, 45);

        tripCreateRequest request = new tripCreateRequest(
                date, departure, arrival, 100L, 200L
        );

        Trip trip = tripMapper.toEntity(request);
        trip.setId(999L);
        trip.setRoute(Route.builder()
                .id(100L)
                .code("TEST")
                .name("Test Route")
                .origin("Origin City")
                .destination("Destination City")
                .build());
        trip.setBus(Bus.builder()
                .id(200L)
                .plate("TEST-999")
                .capacity(50)
                .build());

        tripResponse response = tripMapper.toResponse(trip);

        assertEquals(999L, response.id());
        assertEquals(date, response.date());
        assertEquals(departure, response.departureAt());
        assertEquals(arrival, response.arrivalEta());
        assertEquals("SCHEDULED", response.statusTrip());
        assertEquals(100L, response.routeId());
        assertEquals("Test Route", response.routeName());
        assertEquals("Origin City", response.origin());
        assertEquals("Destination City", response.destination());
        assertEquals(200L, response.busId());
        assertEquals("TEST-999", response.busPlate());
        assertEquals(50, response.capacity());
    }

    @Test
    @DisplayName("Debe manejar un viaje de corta duración en el mismo día")
    void HandleSameDayTripWithShortDuration() {
        LocalDate date = LocalDate.now();
        LocalDateTime departure = LocalDateTime.now().withHour(10).withMinute(0);
        LocalDateTime arrival = departure.plusMinutes(45);

        tripCreateRequest request = new tripCreateRequest(
                date, departure, arrival, 1L, 1L
        );

        Trip trip = tripMapper.toEntity(request);

        assertEquals(date, trip.getDate());
        assertEquals(departure, trip.getDepartureAt());
        assertEquals(arrival, trip.getArrivalEta());
    }
}