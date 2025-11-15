package com.unimag.repository;

import com.unimag.AbstractRepositoryTest;
import com.unimag.entities.Bus;
import com.unimag.entities.Enums.StatusBus;
import com.unimag.entities.Enums.Type;
import com.unimag.entities.Seat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


class SeatRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BusRepository busRepository;

    @Test
    @DisplayName("Buscar todos los seat de un id de bus")
    void findByBusId() {

        var bus = createBus("BUS-MULTI");
        var savedBus = busRepository.save(bus);

        seatRepository.save(createSeat(savedBus, "A1", Type.STANDARD));
        seatRepository.save(createSeat(savedBus, "A2", Type.STANDARD));
        seatRepository.save(createSeat(savedBus, "B1", Type.PREFERENTIAL));

        Optional<Seat> seats = seatRepository.findByBusId(savedBus.getId());

        assertThat(seats).hasSameClassAs(3);
    }

    @Test
    @DisplayName("Buscar los seat de un bus y ordenarlos por number")
    void findByBusIdOrderByNumberAsc() {

        Bus bus = createBus("BUS-ORDER");
        Bus savedBus = busRepository.save(bus);

        seatRepository.save(createSeat(savedBus, "C1", Type.STANDARD));
        seatRepository.save(createSeat(savedBus, "A1", Type.STANDARD));
        seatRepository.save(createSeat(savedBus, "B1", Type.STANDARD));

        List<Seat> seats = seatRepository.findByBusIdOrderByNumberAsc(savedBus.getId());

        assertThat(seats).hasSize(3);
        assertThat(seats.get(0).getNumber()).isEqualTo("A1");
        assertThat(seats.get(1).getNumber()).isEqualTo("B1");
        assertThat(seats.get(2).getNumber()).isEqualTo("C1");
    }

    @Test
    @DisplayName("Buscar un seat por id de bus y number")
    void findByBusIdAndNumber() {

        Bus bus = createBus("BUS-FIND");
        Bus savedBus = busRepository.save(bus);
        seatRepository.save(createSeat(savedBus, "D5", Type.STANDARD));

        Optional<Seat> found = seatRepository.findByBusIdAndNumber(savedBus.getId(), "D5");

        assertThat(found).isPresent();
        assertThat(found.get().getNumber()).isEqualTo("D5");
    }

    @Test
    @DisplayName("Buscar seat por id de bus y type")
    void findByBusIdAndType() {

        Bus bus = createBus("BUS-TYPE");
        Bus savedBus = busRepository.save(bus);

        seatRepository.save(createSeat(savedBus, "A1", Type.STANDARD));
        seatRepository.save(createSeat(savedBus, "A2", Type.STANDARD));
        seatRepository.save(createSeat(savedBus, "B1", Type.PREFERENTIAL));

        List<Seat> standardSeats = seatRepository.findByBusIdAndType(
                savedBus.getId(), Type.STANDARD);
        List<Seat> preferentialSeats = seatRepository.findByBusIdAndType(
                savedBus.getId(), Type.PREFERENTIAL);

        assertThat(standardSeats).hasSize(2);
        assertThat(preferentialSeats).hasSize(1);
    }

    @Test@DisplayName("Contar el total de asientos de un bus")
    void countByBusId() {

        Bus bus = createBus("BUS-COUNT");
        Bus savedBus = busRepository.save(bus);

        seatRepository.save(createSeat(savedBus, "A1", Type.STANDARD));
        seatRepository.save(createSeat(savedBus, "A2", Type.STANDARD));
        seatRepository.save(createSeat(savedBus, "A3", Type.STANDARD));

        long count = seatRepository.countByBusId(savedBus.getId());
        assertThat(count).isEqualTo(3);
    }

    private Bus createBus(String plate) {
        var bus =  Bus.builder()
                .plate(plate)
                .capacity(40)
                .statusBus(StatusBus.ACTIVE)
                .build();
        return bus;
    }

    private Seat createSeat(Bus bus, String number, Type type) {
        var seat = Seat.builder()
                .bus(bus)
                .number(number)
                .type(type)
                .build();
        return seat;
    }
}