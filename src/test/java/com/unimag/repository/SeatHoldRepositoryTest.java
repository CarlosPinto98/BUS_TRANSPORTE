package com.unimag.repository;

import com.unimag.AbstractRepositoryTest;
import com.unimag.entities.*;
import com.unimag.entities.Enums.Role;
import com.unimag.entities.Enums.StatusBus;
import com.unimag.entities.Enums.StatusSeatHold;
import com.unimag.entities.Enums.StatusTrip;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SeatHoldRepositoryTest  extends AbstractRepositoryTest {

    @Autowired
    private SeatHoldRepository seatHoldRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusRepository busRepository;


    @Test
    @DisplayName("Buscar seatHold por viaje, asiento y estado")
    void findByTripIdAndSeatNumberAndStatusSeatHold() {

        Trip trip = createAndSaveTrip("ROUTE-321","BUS-321");
        User user = createAndSaveUser("find@example.com", "3002222222");

        SeatHold seatHold = createSeatHold(trip, user, "B2");
        seatHoldRepository.save(seatHold);

        Optional<SeatHold> found = seatHoldRepository.findByTripIdAndSeatNumberAndStatusSeatHold(
                trip.getId(), "B2", StatusSeatHold.HOLD);

        assertThat(found).isPresent();
        assertThat(found.get().getSeatNumber()).isEqualTo("B2");
    }

    @Test
    @DisplayName("Buscar seatHold por id de trip y status")
    void findByTripIdAndStatusSeatHold() {

        Trip trip = createAndSaveTrip("ROUTE-381","BUS-147");
        User user1 = createAndSaveUser("user1@example.com", "3001111111");
        User user2 = createAndSaveUser("user2@example.com", "3002222222");

        SeatHold hold1 = createSeatHold(trip, user1, "C1");
        SeatHold hold2 = createSeatHold(trip, user2, "C2");
        SeatHold expired = createSeatHold(trip, user2, "C3");
        expired.setStatusSeatHold(StatusSeatHold.EXPIRED);

        seatHoldRepository.saveAll(List.of(hold1, hold2, expired));
        List<SeatHold> activeHolds = seatHoldRepository.findByTripIdAndStatusSeatHold(
                trip.getId(), StatusSeatHold.HOLD);

        assertThat(activeHolds).hasSize(2);
        assertThat(activeHolds).extracting(SeatHold::getSeatNumber)
                .containsExactlyInAnyOrder("C1", "C2");
    }

    @Test
    @DisplayName("Buscar seatHold por id de usuario y status")
    void findByUserIdAndStatusSeatHold() {

        Trip trip1 = createAndSaveTrip("ROUTE-921","BUS-369");
        Trip trip2 = createAndSaveTrip("ROUTE-821","BUS-258");
        User user = createAndSaveUser("multi@example.com", "3003333333");

        SeatHold hold1 = createSeatHold(trip1, user, "D1");
        SeatHold hold2 = createSeatHold(trip2, user, "D2");

        seatHoldRepository.saveAll(List.of(hold1, hold2));

        List<SeatHold> userHolds = seatHoldRepository.findByUserIdAndStatusSeatHold(
                user.getId(), StatusSeatHold.HOLD);

        assertThat(userHolds).hasSize(2);
        assertThat(userHolds).allMatch(h -> h.getUser().getId().equals(user.getId()));
    }

    @Test
    @DisplayName("Buscar y eliminar seatHold antiguas o expired")
    void findExpiredHolds() {

        Trip trip = createAndSaveTrip("ROUTE-329","BUS-852");
        User user = createAndSaveUser("bulk@example.com", "3005555555");

        LocalDateTime now = LocalDateTime.now();
        SeatHold expiredHold1 = createSeatHold(trip, user, "F1");
        expiredHold1.setExpiresAt(now.minusMinutes(10));

        SeatHold expiredHold2 = createSeatHold(trip, user, "F2");
        expiredHold2.setExpiresAt(now.minusMinutes(5));

        SeatHold activeHold = createSeatHold(trip, user, "F3");
        activeHold.setExpiresAt(now.plusMinutes(5));

        seatHoldRepository.saveAll(List.of(expiredHold1, expiredHold2, activeHold));

        int expiredCount = seatHoldRepository.expireOldHolds(now);
        assertThat(expiredCount).isEqualTo(2);
    }

    @Test
    @DisplayName("Buscar seatHold ya expired")
    void ExpiredHolds() {

        Trip trip = createAndSaveTrip("ROUTE-351","BUS-963");
        User user = createAndSaveUser("expired@example.com", "3004444444");

        LocalDateTime now = LocalDateTime.now();
        SeatHold expiredHold = createSeatHold(trip, user, "E1");
        expiredHold.setExpiresAt(now.minusMinutes(5));

        SeatHold activeHold = createSeatHold(trip, user, "E2");
        activeHold.setExpiresAt(now.plusMinutes(5));

        seatHoldRepository.saveAll(List.of(expiredHold, activeHold));

        List<SeatHold> expired = seatHoldRepository.findExpiredHolds(now);

        assertThat(expired).hasSize(1);
        assertThat(expired.get(0).getSeatNumber()).isEqualTo("E1");
    }

    @Test
    @DisplayName("Verificar si existe un seatHold activa para un seat")
    void shouldCheckIfSeatHoldExists() {
        Trip trip = createAndSaveTrip("ROUTE-331","BUS-149");
        User user = createAndSaveUser("check@example.com", "3006666666");

        SeatHold seatHold = createSeatHold(trip, user, "G1");
        seatHoldRepository.save(seatHold);

        boolean exists = seatHoldRepository.existsByTripIdAndSeatNumberAndStatusSeatHold(
                trip.getId(), "G1", StatusSeatHold.HOLD);
        boolean notExists = seatHoldRepository.existsByTripIdAndSeatNumberAndStatusSeatHold(
                trip.getId(), "G2", StatusSeatHold.HOLD);

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Contar la cantidad de seatHold en un trip")
    void countActiveHoldsByTrip() {

        Trip trip = createAndSaveTrip("ROUTE-881","BUS-357");
        User user = createAndSaveUser("count@example.com", "3007777777");

        seatHoldRepository.saveAll(List.of(
                createSeatHold(trip, user, "H1"),
                createSeatHold(trip, user, "H2"),
                createSeatHold(trip, user, "H3")
        ));

        long count = seatHoldRepository.countActiveHoldsByTrip(trip.getId());

        assertThat(count).isEqualTo(3);
    }

    private Trip createAndSaveTrip(String codeRoute,String busCode) {
        var route = Route.builder()
                .code(codeRoute)
                .name("Test Route")
                .origin("Origin")
                .destination("Destination")
                .distanceKm(100)
                .durationMin(120)
                .build();
        var savedRoute = routeRepository.save(route);

        var bus = Bus.builder()
                .plate(busCode)
                .capacity(40)
                .statusBus(StatusBus.ACTIVE)
                .build();
        var savedBus = busRepository.save(bus);

        var trip = Trip.builder()
                .route(savedRoute)
                .bus(savedBus)
                .date(LocalDate.now())
                .departureAt(LocalDateTime.now().plusHours(2))
                .arrivalEta(LocalDateTime.now().plusHours(10))
                .statusTrip(StatusTrip.SCHEDULED)
                .build();
        return tripRepository.save(trip);
    }

    private User createAndSaveUser(String email, String phone) {
        var user = User.builder()
                .name("Test User")
                .email(email)
                .phone(phone)
                .role(Role.PASSENGER)
                .passwordHash("hash")
                .build();
        return userRepository.save(user);
    }

    private SeatHold createSeatHold(Trip trip, User user, String seatNumber) {
        var seatHold = SeatHold.builder()
                .trip(trip)
                .user(user)
                .seatNumber(seatNumber)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .statusSeatHold(StatusSeatHold.HOLD)
                .build();
        return seatHold;
    }
}