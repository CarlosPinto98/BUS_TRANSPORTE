package com.unimag.repository;

import com.unimag.AbstractRepositoryTest;
import com.unimag.entities.Bus;
import com.unimag.entities.Enums.StatusBus;
import com.unimag.entities.Enums.StatusTrip;
import com.unimag.entities.Route;
import com.unimag.entities.Trip;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TripRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusRepository busRepository;

    @Test
    @DisplayName("Guardar un trip")
    void shouldSaveTrip() {
        var route = createAndSaveRoute();
        var bus = createAndSaveBus();
        var today = LocalDate.now();

        var trip = createTrip(route,bus,
                today,
                today.atTime(2,0),
                today.atTime(6,0),
                StatusTrip.SCHEDULED);
        Trip saved = tripRepository.save(trip);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatusTrip()).isEqualTo(StatusTrip.SCHEDULED);
    }

    @Test
    @DisplayName("Buscar trip por id, fecha y estado de route")
    void findByRouteIdAndDate() {

        var route = createAndSaveRoute();
        var bus = createAndSaveBus();
        var today = LocalDate.now();

        tripRepository.save(createTrip(route, bus, today,
                today.atTime(2,0),
                today.atTime(7,0),
                StatusTrip.SCHEDULED
        ));
        tripRepository.save(createTrip(route, bus, today,
                today.atTime(1,0),
                today.atTime(3,0),
                StatusTrip.SCHEDULED
        ));
        tripRepository.save(createTrip(route, bus, today,
                today.atTime(3,0),
                today.atTime(9,0),
                StatusTrip.SCHEDULED
        ));

        List<Trip> todayTrips = tripRepository.findByRouteIdAndDate(route.getId(), today);

        assertThat(todayTrips).hasSize(3);
        assertThat(todayTrips).allMatch(t -> t.getDate().equals(today));
    }

    @Test
    @DisplayName("Buscar trip por route, fecha y estado")
    void findByRouteIdAndDateAndStatusTrip() {

        var route = createAndSaveRoute();
        var bus = createAndSaveBus();
        var today = LocalDate.now();

        var scheduled = createTrip(route, bus, today,
                today.atTime(1,0),
                today.atTime(4,0),
                StatusTrip.SCHEDULED
        );
        var departed = createTrip(route, bus, today,
                today.atTime(1,0),
                today.atTime(2,0),
                StatusTrip.DEPARTED
        );

        tripRepository.saveAll(List.of(scheduled, departed));

        List<Trip> scheduledTrips = tripRepository.findByRouteIdAndDateAndStatusTrip(
                route.getId(), today, StatusTrip.SCHEDULED);

        assertThat(scheduledTrips).hasSize(1);
        assertThat(scheduledTrips.get(0).getStatusTrip()).isEqualTo(StatusTrip.SCHEDULED);
    }

    @Test
    @DisplayName("Buscar trip por fecha y estado")
    void findByDateAndStatusTrip() {

        var route = createAndSaveRoute();
        var bus = createAndSaveBus();
        var today = LocalDate.now();

        tripRepository.save(createTrip(route, bus, today,
                today.atTime(1,0),
                today.atTime(3,0),
                StatusTrip.SCHEDULED
        ));
        tripRepository.save(createTrip(route, bus, today,
                today.atTime(2,0),
                today.atTime(3,0),
                StatusTrip.SCHEDULED
        ));

        List<Trip> trips = tripRepository.findByDateAndStatusTrip(today,StatusTrip.SCHEDULED);

        assertThat(trips).hasSize(2);
    }

    @Test
    @DisplayName("Buscar trip por id")
    void findByIdWithDetails() {

        var route = createAndSaveRoute();
        var bus = createAndSaveBus();
        var today = LocalDate.now();
        var trip = createTrip(route, bus, today,
                today.atTime(2,0),
                today.atTime(3,0),
                StatusTrip.SCHEDULED
        );
        Trip saved = tripRepository.save(trip);

        Optional<Trip> found = tripRepository.findByIdWithDetails(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getRoute()).isNotNull();
        assertThat(found.get().getBus()).isNotNull();
        assertThat(found.get().getRoute().getName()).isNotEmpty();
    }

    @Test
    @DisplayName("Buscar trip por rango hora")
    void findByDateAndTimeRange() {

        var route = createAndSaveRoute();
        var bus = createAndSaveBus();
        var today = LocalDate.now();

        tripRepository.save(createTrip(route, bus, today,
                today.atTime(8,0),
                today.atTime(8,0),
                StatusTrip.SCHEDULED
        ));
        tripRepository.save(createTrip(route, bus, today,
                today.atTime(14,0),
                today.atTime(8,0),
                StatusTrip.SCHEDULED
        ));
        tripRepository.save(createTrip(route, bus, today,
                today.atTime(20,0),
                today.atTime(8,0),
                StatusTrip.SCHEDULED
        ));

        List<Trip> trips = tripRepository.findByDateAndTimeRange(
                today,
                StatusTrip.SCHEDULED,
                today.atTime(7, 0),
                today.atTime(15, 0));

        assertThat(trips).hasSize(2);
    }

    @Test
    @DisplayName("Buscar trip por bus y fecha")
    void findActiveTripsByBusAndDate() {

        var route = createAndSaveRoute();
        var bus = createAndSaveBus();
        var today = LocalDate.now();

        var scheduled = createTrip(route, bus, today,
                today.atTime(10,0),
                today.atTime(18,0),
                StatusTrip.SCHEDULED
        );
        var boarding = createTrip(route, bus, today,
                today.atTime(10,0),
                today.atTime(18,0),
                StatusTrip.BOARDING
        );
        var cancelled = createTrip(route, bus, today,
                today.atTime(14,0),
                today.atTime(8,0),
                StatusTrip.CANCELLED
        );

        tripRepository.saveAll(List.of(scheduled, boarding, cancelled));

        List<Trip> activeTrips = tripRepository.findActiveTripsByBusAndDate(bus.getId(), today);

        assertThat(activeTrips).hasSize(2);
        assertThat(activeTrips).extracting(Trip::getStatusTrip)
                .containsExactlyInAnyOrder(StatusTrip.SCHEDULED, StatusTrip.BOARDING);
    }

    @Test
    @DisplayName("Buscar trip por estado y hora dada")
    void findByStatusTripAndDepartureAtBefore() {

        var route = createAndSaveRoute();
        var bus = createAndSaveBus();
        var today = LocalDate.now();
        var departureAt = LocalDateTime.now().minusHours(2);

        var pastTrip = createTrip(route, bus, today,
                departureAt,
                departureAt.plusHours(8),
                StatusTrip.SCHEDULED
        );
        var futureTrip = createTrip(route, bus, today,
                departureAt,
                departureAt.plusHours(8),
                StatusTrip.SCHEDULED
        );

        tripRepository.saveAll(List.of(pastTrip, futureTrip));
        List<Trip> trips = tripRepository.findByStatusTripAndDepartureAtBefore(
                StatusTrip.SCHEDULED, LocalDateTime.now());

        assertThat(trips).hasSize(2);
    }

    private Bus createAndSaveBus() {
        var bus = Bus.builder()
                .plate("BUS-0954")
                .capacity(40)
                .statusBus(StatusBus.ACTIVE)
                .build();
        return busRepository.save(bus);
    }
    private Trip createTrip(Route route, Bus bus, LocalDate date, LocalDateTime departureAt, LocalDateTime arrivalEta, StatusTrip tripStatus) {
        var trip = Trip.builder()
                .route(route)
                .bus(bus)
                .date(date)
                .departureAt(departureAt)
                .arrivalEta(departureAt)
                .statusTrip(tripStatus)
                .build();
        return trip;
    }

    private Route createAndSaveRoute() {
        var route = Route.builder()
                .code("ROUTE-0544")
                .name("Test Route")
                .origin("Origin")
                .destination("Destination")
                .distanceKm(100)
                .durationMin(120)
                .build();
        return routeRepository.save(route);
    }
}