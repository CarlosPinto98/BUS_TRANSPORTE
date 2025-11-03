package com.unimag.repository;

import com.unimag.AbstractRepositoryTest;
import com.unimag.entities.Bus;
import com.unimag.entities.Enums.StatusBus;
import com.unimag.entities.Enums.Type;
import com.unimag.entities.Seat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class BusRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private BusRepository busRepository;

    @Test
    @DisplayName("Buscar bus por plate")
    void findByPlate() {

        var bus =createBus("XYZ789",45,StatusBus.ACTIVE);
        busRepository.save(bus);

        Optional<Bus> found = busRepository.findByPlate("XYZ789");

        assertThat(found).isPresent();
        assertThat(found.get().getCapacity()).isEqualTo(45);

    }

    @Test
    @DisplayName("Listar buses por status")
    void findByStatusBus() {

        Bus activeBus1 = createBus("ACTIVE1",40, StatusBus.ACTIVE);
        Bus activeBus2 = createBus("ACTIVE2", 35, StatusBus.ACTIVE);
        Bus maintenanceBus = createBus("MAINT", 50 , StatusBus.MAINTENANCE);

        busRepository.saveAll(List.of(activeBus1, activeBus2, maintenanceBus));

        List<Bus> activeBuses = busRepository.findByStatusBus(StatusBus.ACTIVE);

        assertThat(activeBuses).hasSize(2);
        assertThat(activeBuses).extracting(Bus::getStatusBus)
                .containsOnly(StatusBus.ACTIVE);
    }

    @Test
    @DisplayName("Buscar bus por id, incluyendo sus seats")
    void findByIdWithSeats() {

        var bus = createBus("BUS-SEATS",40, StatusBus.ACTIVE);

        var seat1 = Seat.builder()
                .bus(bus)
                .number("A1")
                .type(Type.STANDARD)
                .build();
        var seat2 = Seat.builder()
                .bus(bus)
                .number("A2")
                .type(Type.STANDARD)
                .build();
        var seat3 = Seat.builder()
                .bus(bus)
                .number("A3")
                .type(Type.PREFERENTIAL)
                .build();
        var seat4 = Seat.builder()
                .bus(bus)
                .number("A4")
                .type(Type.PREFERENTIAL)
                .build();

        bus.getSeats().addAll(List.of(seat1, seat2, seat3, seat4));
        Bus saved = busRepository.save(bus);

        Optional<Bus> found = busRepository.findByIdWithSeats(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getSeats()).hasSize(4);
        assertThat(found.get().getSeats()).extracting(Seat::getNumber)
                .containsExactlyInAnyOrder("A1", "A2", "A3", "A4");
    }

    @Test
    @DisplayName("Buscar buses activos con capacidad minima")
    void findAvailableBusesByCapacity() {

        Bus smallBus = createBus("ANB233",20 ,StatusBus.ACTIVE);

        Bus mediumBus = createBus("ALL234", 40, StatusBus.ACTIVE);

        Bus largeBus = createBus("LAR344", 50 ,StatusBus.ACTIVE);

        Bus inactiveBus = createBus("PZR456", 60, StatusBus.INACTIVE);


        busRepository.saveAll(List.of(smallBus, mediumBus, largeBus, inactiveBus));

        List<Bus> buses = busRepository.findAvailableBusesByCapacity(
                StatusBus.ACTIVE, 35);

        assertThat(buses).hasSize(2);
        assertThat(buses).extracting(Bus::getPlate)
                .containsExactlyInAnyOrder("ALL234", "LAR344");
    }

    @Test
    @DisplayName("Verificar si una plate ya existe")
    void existsByPlate() {

        Bus bus = createBus("CHE123", 40, StatusBus.ACTIVE);
        busRepository.save(bus);

        boolean exists = busRepository.existsByPlate("CHE123");
        boolean notExists = busRepository.existsByPlate("NAW123");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Guardar un bus")
    void shouldSaveBus() {
        var bus = createBus("ABC123",40, StatusBus.ACTIVE);

        Map<String, Object> amenities = new HashMap<>();
        amenities.put("wifi", true);
        amenities.put("ac", true);
        bus.setAmenities(amenities);

        Bus saved = busRepository.save(bus);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPlate()).isEqualTo("ABC123");
        assertThat(saved.getAmenities()).containsKey("wifi");
    }

    private Bus createBus(String plate,Integer capacity, StatusBus statusBus) {
        var bus =  Bus.builder()
                .plate(plate)
                .capacity(capacity)
                .statusBus(statusBus)
                .build();
        return bus;
    }
}