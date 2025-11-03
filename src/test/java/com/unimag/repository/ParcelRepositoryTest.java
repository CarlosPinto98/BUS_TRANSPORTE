package com.unimag.repository;

import com.unimag.AbstractRepositoryTest;
import com.unimag.entities.*;
import com.unimag.entities.Enums.StatusBus;
import com.unimag.entities.Enums.StatusParcel;
import com.unimag.entities.Enums.StatusTrip;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


class ParcelRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private StopRepository stopRepository;

    @Test
    @DisplayName("Buscr parcel por code")
    void findByCode() {

        Trip trip = createAndSaveTrip();
        Stop fromStop = createAndSaveStop(trip.getRoute(), "A", 1);
        Stop toStop = createAndSaveStop(trip.getRoute(), "B", 2);

        Parcel parcel = createParcel("FIND123", fromStop, toStop, trip);
        parcelRepository.save(parcel);

        Optional<Parcel> found = parcelRepository.findByCode("FIND123");

        assertThat(found).isPresent();
        assertThat(found.get().getSenderName()).isEqualTo("Juan Sender");
    }

    @Test
    @DisplayName("Buscar paquetes por status")
    void findByStatusParcel() {

        Trip trip = createAndSaveTrip();
        Stop fromStop = createAndSaveStop(trip.getRoute(), "X", 1);
        Stop toStop = createAndSaveStop(trip.getRoute(), "Y", 2);

        Parcel created = createParcel("PCL001", fromStop, toStop, trip);
        Parcel inTransit = createParcel("PCL002", fromStop, toStop, trip);
        inTransit.setStatusParcel(StatusParcel.IN_TRANSIT);
        Parcel delivered = createParcel("PCL003", fromStop, toStop, trip);
        delivered.setStatusParcel(StatusParcel.DELIVERED);

        parcelRepository.saveAll(List.of(created, inTransit, delivered));

        List<Parcel> inTransitParcels = parcelRepository.findByStatusParcel(StatusParcel.IN_TRANSIT);

        assertThat(inTransitParcels).hasSize(1);
        assertThat(inTransitParcels.get(0).getCode()).isEqualTo("PCL002");
    }

    @Test
    @DisplayName("Buscar parcel asignado a un id de viaje")
    void findByTripId() {

        Trip trip = createAndSaveTrip();
        Stop fromStop = createAndSaveStop(trip.getRoute(), "P", 1);
        Stop toStop = createAndSaveStop(trip.getRoute(), "Q", 2);

        parcelRepository.saveAll(List.of(
                createParcel("T1", fromStop, toStop, trip),
                createParcel("T2", fromStop, toStop, trip),
                createParcel("T3", fromStop, toStop, trip)
        ));

        List<Parcel> parcels = parcelRepository.findByTripId(trip.getId());
        assertThat(parcels).hasSize(3);
    }

    @Test
    @DisplayName("Buscar parcel asignado a un id de viaje")
    void findByTripIdAndStatusParcel() {

        Trip trip = createAndSaveTrip();
        Stop fromStop = createAndSaveStop(trip.getRoute(), "P", 1);
        Stop toStop = createAndSaveStop(trip.getRoute(), "Q", 2);

        parcelRepository.saveAll(List.of(
                createParcel("T1", fromStop, toStop, trip),
                createParcel("T2", fromStop, toStop, trip),
                createParcel("T3", fromStop, toStop, trip)
        ));

        List<Parcel> parcels = parcelRepository.findByTripId(trip.getId());
        assertThat(parcels).hasSize(3);
    }

    @Test
    @DisplayName("Buscar paquete asociado a un phone")
    void findByPhone() {

        Trip trip = createAndSaveTrip();
        Stop fromStop = createAndSaveStop(trip.getRoute(), "R", 1);
        Stop toStop = createAndSaveStop(trip.getRoute(), "S", 2);

        Parcel asSender = createParcel("PS1", fromStop, toStop, trip);
        asSender.setSenderPhone("3001111111");
        asSender.setReceiverPhone("3009999999");

        Parcel asReceiver = createParcel("PS2", fromStop, toStop, trip);
        asReceiver.setSenderPhone("3008888888");
        asReceiver.setReceiverPhone("3001111111");
        parcelRepository.saveAll(List.of(asSender, asReceiver));

        List<Parcel> parcels = parcelRepository.findByPhone("3001111111");
        assertThat(parcels).hasSize(2);
    }

    @Test
    @DisplayName("Buscar entregas pendientes para un destinario")
    void findPendingDeliveriesByReceiver() {

        Trip trip = createAndSaveTrip();
        Stop fromStop = createAndSaveStop(trip.getRoute(), "T", 1);
        Stop toStop = createAndSaveStop(trip.getRoute(), "U", 2);

        Parcel pending = createParcel("PD1", fromStop, toStop, trip);
        pending.setReceiverPhone("3002222222");
        pending.setStatusParcel(StatusParcel.IN_TRANSIT);

        Parcel delivered = createParcel("PD2", fromStop, toStop, trip);
        delivered.setReceiverPhone("3002222222");
        delivered.setStatusParcel(StatusParcel.DELIVERED);

        parcelRepository.saveAll(List.of(pending, delivered));

        List<Parcel> pendingDeliveries = parcelRepository.findPendingDeliveriesByReceiver("3002222222");

        assertThat(pendingDeliveries).hasSize(1);
        assertThat(pendingDeliveries.get(0).getCode()).isEqualTo("PD1");
    }

    @Test
    @DisplayName("Contar paquetes en transito para viaje")
    void countInTransitParcelsByTrip() {

        Trip trip = createAndSaveTrip();
        Stop fromStop = createAndSaveStop(trip.getRoute(), "V", 1);
        Stop toStop = createAndSaveStop(trip.getRoute(), "W", 2);

        Parcel p1 = createParcel("IT1", fromStop, toStop, trip);
        p1.setStatusParcel(StatusParcel.IN_TRANSIT);

        Parcel p2 = createParcel("IT2", fromStop, toStop, trip);
        p2.setStatusParcel(StatusParcel.IN_TRANSIT);

        Parcel p3 = createParcel("IT3", fromStop, toStop, trip);
        p3.setStatusParcel(StatusParcel.DELIVERED);

        parcelRepository.saveAll(List.of(p1, p2, p3));

        long count = parcelRepository.countInTransitParcelsByTrip(trip.getId());

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Buscar si existe code de paquete")
    void existsByCode() {

        Trip trip = createAndSaveTrip();
        Stop fromStop = createAndSaveStop(trip.getRoute(), "M", 1);
        Stop toStop = createAndSaveStop(trip.getRoute(), "N", 2);

        Parcel parcel = createParcel("EXISTS", fromStop, toStop, trip);
        parcelRepository.save(parcel);

        boolean exists = parcelRepository.existsByCode("EXISTS");
        boolean notExists = parcelRepository.existsByCode("NOTEXISTS");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Guardar un parcel")
    void shouldSaveParcel() {
        Trip trip = createAndSaveTrip();
        Stop fromStop = createAndSaveStop(trip.getRoute(), "Origin", 1);
        Stop toStop = createAndSaveStop(trip.getRoute(), "Destination", 2);

        Parcel parcel = createParcel("PCL001", fromStop, toStop, trip);
        Parcel saved = parcelRepository.save(parcel);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCode()).isEqualTo("PCL001");
        assertThat(saved.getStatusParcel()).isEqualTo(StatusParcel.CREATED);
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    private Trip createAndSaveTrip() {
        Route route =  Route.builder()
                .code("ROUTE-784")
                .name("Test Route")
                .origin("Origin")
                .destination("Destination")
                .distanceKm(100)
                .durationMin(120)
                .build();
        Route savedRoute = routeRepository.save(route);

        Bus bus = Bus.builder()
                .plate("BUS-" + System.currentTimeMillis())
                .capacity(40)
                .statusBus(StatusBus.ACTIVE)
                .build();
        Bus savedBus = busRepository.save(bus);

        Trip trip = Trip.builder()
                .route(savedRoute)
                .bus(savedBus)
                .date(LocalDate.now())
                .departureAt(LocalDateTime.now().plusHours(2))
                .arrivalEta(LocalDateTime.now().plusHours(10))
                .statusTrip(StatusTrip.SCHEDULED)
                .build();
        return tripRepository.save(trip);
    }

    private Stop createAndSaveStop(Route route, String name, Integer order) {
        var stop =  Stop.builder()
                .route(route)
                .name(name)
                .order(order)
                .lat(new BigDecimal("4.0"))
                .lng(new BigDecimal("-74.0"))
                .build();
        return stopRepository.save(stop);
    }

    private Parcel createParcel(String code, Stop fromStop, Stop toStop, Trip trip) {
        var parcel = Parcel.builder()
                .code(code)
                .senderName("Juan Sender")
                .senderPhone("3001111111")
                .receiverName("Maria Receiver")
                .receiverPhone("3002222222")
                .fromStop(fromStop)
                .toStop(toStop)
                .trip(trip)
                .price(new BigDecimal("15000"))
                .statusParcel(StatusParcel.CREATED)
                .deliveryOtp("123456")
                .createdAt(LocalDateTime.now())
                .deliveredAt(LocalDateTime.now().plusHours(2))
                .build();
        return parcel;
    }
}