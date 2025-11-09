package com.unimag.mappers;

import com.unimag.DTO.SeatHoldDTO.*;
import com.unimag.entities.Enums.StatusSeatHold;
import com.unimag.entities.Route;
import com.unimag.entities.SeatHold;
import com.unimag.entities.Trip;
import com.unimag.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("SeatHoldMapper Tests")
class SeatHoldMapperTest {

    private SeatHoldMapper seatHoldMapper;

    @BeforeEach
    void setUp() {
        seatHoldMapper = Mappers.getMapper(SeatHoldMapper.class);
    }

    @Test
    @DisplayName("Debe mapear SeatHoldCreateRequest a la entidad SeatHold")
    void toEntity() {

        SeatHoldCreateRequest request = new SeatHoldCreateRequest(
                1L,
                "1A",
                2L,
                3L
        );

        SeatHold seatHold = seatHoldMapper.toEntity(request);

        assertNotNull(seatHold);
        assertEquals("1A", seatHold.getSeatNumber());
        assertEquals(StatusSeatHold.HOLD, seatHold.getStatusSeatHold());
        assertNull(seatHold.getId());
        assertNull(seatHold.getUser());
        assertNull(seatHold.getExpiresAt());
    }

    @Test
    @DisplayName("Debe actualizar la entidad SeatHold desde SeatHoldUpdateRequest")
    void updateEntity() {

        SeatHold existingSeatHold = SeatHold.builder()
                .id(1L)
                .seatNumber("1A")
                .statusSeatHold(StatusSeatHold.HOLD)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        SeatHoldUpdateRequest request = new SeatHoldUpdateRequest(
                StatusSeatHold.EXPIRED
        );

        seatHoldMapper.updateEntity(request, existingSeatHold);

        assertEquals(StatusSeatHold.EXPIRED, existingSeatHold.getStatusSeatHold());
        assertEquals("1A", existingSeatHold.getSeatNumber());
    }

    @Test
    @DisplayName("Debe mapear la entidad SeatHold a SeatHoldResponse")
    void toResponse() {

        Route route = Route.builder()
                .id(1L)
                .name("Bogotá - Tunja")
                .build();

        Trip trip = Trip.builder()
                .id(1L)
                .date(LocalDate.of(2025, 12, 25))
                .departureAt(LocalDateTime.of(2025, 12, 25, 8, 0))
                .route(route)
                .build();

        User user = User.builder()
                .id(2L)
                .name("John Doe")
                .build();

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);
        LocalDateTime createdAt = LocalDateTime.now();

        SeatHold seatHold = SeatHold.builder()
                .id(1L)
                .trip(trip)
                .seatNumber("1A")
                .user(user)
                .expiresAt(expiresAt)
                .statusSeatHold(StatusSeatHold.HOLD)
                .createdAt(createdAt)
                .build();

        SeatHoldResponse response = seatHoldMapper.toResponse(seatHold);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("1A", response.seatNumber());
        assertEquals(expiresAt, response.expiresAt());
        assertEquals("HOLD", response.statusSeatHold());
        assertEquals(createdAt, response.createdAt());
        assertEquals(1L, response.tripId());
        assertEquals(2L, response.userId());
        assertEquals("2025-12-25", response.tripDate());
        assertEquals("08:00", response.tripTime());
        assertEquals("Bogotá - Tunja", response.routeName());
        assertNotNull(response.minutesLeft());
        assertTrue(response.minutesLeft() >= 0);
    }

    @Test
    @DisplayName("Debe mapear todos los tipos de HoldStatus correctamente")
    void shouldMapAllHoldStatusTypes() {
        for (StatusSeatHold status : StatusSeatHold.values()) {
            SeatHold seatHold = SeatHold.builder()
                    .id(1L)
                    .trip(Trip.builder().id(1L)
                            .date(LocalDate.now())
                            .departureAt(LocalDateTime.now())
                            .route(Route.builder().id(1L).name("Test").build())
                            .build())
                    .seatNumber("1A")
                    .user(User.builder().id(1L).build())
                    .expiresAt(LocalDateTime.now().plusMinutes(10))
                    .statusSeatHold(status)
                    .createdAt(LocalDateTime.now())
                    .build();

            SeatHoldResponse response = seatHoldMapper.toResponse(seatHold);

            assertEquals(status.name(), response.statusSeatHold());
        }
    }

    @Test
    @DisplayName("Debe formatear la fecha y hora correctamente")
    void shouldFormatDateAndTimeCorrectly() {
        Trip trip = Trip.builder()
                .id(1L)
                .date(LocalDate.of(2025, 3, 15))
                .departureAt(LocalDateTime.of(2025, 3, 15, 16, 45))
                .route(Route.builder().id(1L).name("Test Route").build())
                .build();

        SeatHold seatHold = SeatHold.builder()
                .id(1L)
                .trip(trip)
                .seatNumber("1A")
                .user(User.builder().id(1L).build())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .statusSeatHold(StatusSeatHold.HOLD)
                .createdAt(LocalDateTime.now())
                .build();

        SeatHoldResponse response = seatHoldMapper.toResponse(seatHold);

        assertEquals("2025-03-15", response.tripDate());
        assertEquals("16:45", response.tripTime());
    }

    @Test
    @DisplayName("Debe calcular correctamente los minutos restantes para una expiración futura")
    void calculateMinutesLeft() {

        LocalDateTime futureExpiration = LocalDateTime.now().plusMinutes(8);

        SeatHold seatHold = SeatHold.builder()
                .id(1L)
                .trip(Trip.builder().id(1L)
                        .date(LocalDate.now())
                        .departureAt(LocalDateTime.now())
                        .route(Route.builder().id(1L).name("Test").build())
                        .build())
                .seatNumber("1A")
                .user(User.builder().id(1L).build())
                .expiresAt(futureExpiration)
                .statusSeatHold(StatusSeatHold.HOLD)
                .createdAt(LocalDateTime.now())
                .build();

        SeatHoldResponse response = seatHoldMapper.toResponse(seatHold);

        assertNotNull(response.minutesLeft());
        assertTrue(response.minutesLeft() >= 7 && response.minutesLeft() <= 8);
    }

    @Test
    @DisplayName("Debe devolver cero minutos restantes para una reserva expirada")
    void shouldReturnZeroMinutesLeftForExpiredHold() {
        LocalDateTime pastExpiration = LocalDateTime.now().minusMinutes(5);

        SeatHold seatHold = SeatHold.builder()
                .id(1L)
                .trip(Trip.builder().id(1L)
                        .date(LocalDate.now())
                        .departureAt(LocalDateTime.now())
                        .route(Route.builder().id(1L).name("Test").build())
                        .build())
                .seatNumber("1A")
                .user(User.builder().id(1L).build())
                .expiresAt(pastExpiration)
                .statusSeatHold(StatusSeatHold.EXPIRED)
                .createdAt(LocalDateTime.now())
                .build();

        SeatHoldResponse response = seatHoldMapper.toResponse(seatHold);

        assertEquals(0, response.minutesLeft());
    }

    @Test
    @DisplayName("Debe manejar expiresAt nulo")
    void shouldHandleNullExpiresAt() {
        SeatHold seatHold = SeatHold.builder()
                .id(1L)
                .trip(Trip.builder().id(1L)
                        .date(LocalDate.now())
                        .departureAt(LocalDateTime.now())
                        .route(Route.builder().id(1L).name("Test").build())
                        .build())
                .seatNumber("1A")
                .user(User.builder().id(1L).build())
                .expiresAt(null)
                .statusSeatHold(StatusSeatHold.HOLD)
                .createdAt(LocalDateTime.now())
                .build();

        SeatHoldResponse response = seatHoldMapper.toResponse(seatHold);

        assertNull(response.minutesLeft());
    }

    @Test
    @DisplayName("Debe manejar diferentes números de asiento")
    void shouldHandleDifferentSeatNumbers() {
        String[] seatNumbers = {"1A", "10B", "VIP-1", "PREF_5", "25"};

        for (String seatNumber : seatNumbers) {
            SeatHoldCreateRequest request = new SeatHoldCreateRequest(
                    1L, seatNumber, 2L, 3L
            );

            SeatHold seatHold = seatHoldMapper.toEntity(request);
            assertEquals(seatNumber, seatHold.getSeatNumber());
        }
    }

    @Test
    @DisplayName("Debe preservar todos los campos durante el ciclo completo de mapeo")
    void shouldPreserveAllFieldsDuringFullCycleMapping() {
        SeatHoldCreateRequest request = new SeatHoldCreateRequest(
                100L, "VIP-10", 200L, 300L
        );

        SeatHold seatHold = seatHoldMapper.toEntity(request);
        seatHold.setId(999L);

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);
        LocalDateTime createdAt = LocalDateTime.now().minusMinutes(5);

        seatHold.setExpiresAt(expiresAt);
        seatHold.setCreatedAt(createdAt);
        seatHold.setUser(User.builder().id(500L).name("Test User").build());
        seatHold.setTrip(Trip.builder()
                .id(100L)
                .date(LocalDate.of(2025, 12, 31))
                .departureAt(LocalDateTime.of(2025, 12, 31, 23, 59))
                .route(Route.builder().id(1L).name("New Year Route").build())
                .build());

        SeatHoldResponse response = seatHoldMapper.toResponse(seatHold);

        assertEquals(999L, response.id());
        assertEquals("VIP-10", response.seatNumber());
        assertEquals(expiresAt, response.expiresAt());
        assertEquals("HOLD", response.statusSeatHold());
        assertEquals(createdAt, response.createdAt());
        assertEquals(100L, response.tripId());
        assertEquals(500L, response.userId());
        assertEquals("2025-12-31", response.tripDate());
        assertEquals("23:59", response.tripTime());
        assertEquals("New Year Route", response.routeName());
        assertNotNull(response.minutesLeft());
    }

    @Test
    @DisplayName("Debe manejar la expiración exacta de 10 minutos")
    void shouldHandleExact10MinuteExpiration() {
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

        SeatHold seatHold = SeatHold.builder()
                .id(1L)
                .trip(Trip.builder().id(1L)
                        .date(LocalDate.now())
                        .departureAt(LocalDateTime.now())
                        .route(Route.builder().id(1L).name("Test").build())
                        .build())
                .seatNumber("1A")
                .user(User.builder().id(1L).build())
                .expiresAt(expiresAt)
                .statusSeatHold(StatusSeatHold.HOLD)
                .createdAt(LocalDateTime.now())
                .build();

        SeatHoldResponse response = seatHoldMapper.toResponse(seatHold);

        assertNotNull(response.minutesLeft());
        assertTrue(response.minutesLeft() >= 9 && response.minutesLeft() <= 10);
    }
}