package com.unimag.mappers;

import com.unimag.DTO.SeatDTO.*;
import com.unimag.entities.Bus;
import com.unimag.entities.Enums.StatusBus;
import com.unimag.entities.Enums.Type;
import com.unimag.entities.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SeatMapper Tests")
class SeatMapperTest {

    private SeatMapper seatMapper;

    @BeforeEach
    void setUp() {
        seatMapper = Mappers.getMapper(SeatMapper.class);
    }

    @Test
    @DisplayName("Debe mapear SeatCreateRequest a la entidad Seat")
    void toEntity() {

        SeatCreateRequest request = new SeatCreateRequest(
                "1A",
                Type.STANDARD,
                1L
        );

        Seat seat = seatMapper.toEntity(request);

        assertNotNull(seat);
        assertEquals("1A", seat.getNumber());
        assertEquals(Type.STANDARD, seat.getType());
        assertNotNull(seat.getBus());
        assertEquals(1L, seat.getBus().getId());
        assertNull(seat.getId());
    }

    @Test
    @DisplayName("Debe actualizar la entidad Seat desde SeatUpdateRequest")
    void updateEntity() {

        Seat existingSeat = Seat.builder()
                .id(1L)
                .number("1A")
                .type(Type.STANDARD)
                .build();

        SeatUpdateRequest request = new SeatUpdateRequest(
                Type.PREFERENTIAL
        );

        seatMapper.updateEntity(request, existingSeat);

        assertEquals(Type.PREFERENTIAL, existingSeat.getType());
        assertEquals("1A", existingSeat.getNumber());
    }

    @Test
    @DisplayName("Debe mapear la entidad Seat a SeatResponse")
    void toResponse() {

        Bus bus = Bus.builder()
                .id(1L)
                .plate("ABC123")
                .capacity(40)
                .amenities(new HashMap<>())
                .statusBus(StatusBus.ACTIVE)
                .build();

        Seat seat = Seat.builder()
                .id(1L)
                .number("1A")
                .type(Type.PREFERENTIAL)
                .bus(bus)
                .build();

        SeatResponse response = seatMapper.toResponse(seat);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("1A", response.number());
        assertEquals("PREFERENTIAL", response.type());
        assertEquals(1L, response.busId());
        assertEquals("ABC123", response.busPlate());
        assertEquals(40, response.busCapacity());
    }

    @Test
    @DisplayName("Debe mapear todos los tipos de SeatType correctamente")
    void mapBus() {

        for (Type type : Type.values()) {
            SeatCreateRequest request = new SeatCreateRequest(
                    "TEST",
                    type,
                    1L
            );

            Seat seat = seatMapper.toEntity(request);
            seat.setId(1L);
            seat.setBus(Bus.builder()
                    .id(1L)
                    .plate("TEST")
                    .capacity(40)
                    .statusBus(StatusBus.ACTIVE)
                    .build());

            SeatResponse response = seatMapper.toResponse(seat);

            assertEquals(type, seat.getType());
            assertEquals(type.name(), response.type());
        }
    }
        @Test
        @DisplayName("Debe manejar diferentes formatos de número de asiento")
        void shouldHandleDifferentSeatNumberFormats() {
            String[] seatNumbers = {"1", "1A", "A1", "10", "10B", "VIP-1", "PREF_5"};

            for (String number : seatNumbers) {
                SeatCreateRequest request = new SeatCreateRequest(
                        number,
                        Type.STANDARD,
                        1L
                );

                Seat seat = seatMapper.toEntity(request);

                assertEquals(number, seat.getNumber());
            }
        }

        @Test
        @DisplayName("Debe preservar la referencia al autobús durante el mapeo")
        void shouldPreserveBusReferenceDuringMapping() {
            Long busId = 42L;
            SeatCreateRequest request = new SeatCreateRequest(
                    "1A",
                    Type.STANDARD,
                    busId
            );

            Seat seat = seatMapper.toEntity(request);

            assertNotNull(seat.getBus());
            assertEquals(busId, seat.getBus().getId());
        }

        @Test
        @DisplayName("Debe mapear el asiento con información completa del autobús")
        void shouldMapSeatWithCompleteBusInformation() {
            Bus bus = Bus.builder()
                    .id(10L)
                    .plate("LUXURY001")
                    .capacity(45)
                    .amenities(new HashMap<>())
                    .statusBus(StatusBus.ACTIVE)
                    .build();

            Seat seat = Seat.builder()
                    .id(5L)
                    .number("VIP-1")
                    .type(Type.PREFERENTIAL)
                    .bus(bus)
                    .build();

            SeatResponse response = seatMapper.toResponse(seat);

            assertEquals(5L, response.id());
            assertEquals("VIP-1", response.number());
            assertEquals("PREFERENTIAL", response.type());
            assertEquals(10L, response.busId());
            assertEquals("LUXURY001", response.busPlate());
            assertEquals(45, response.busCapacity());
        }

        @Test
        @DisplayName("Debe manejar el tipo de asiento 'standard' como predeterminado")
        void shouldHandleStandardSeatTypeAsDefault() {
            SeatCreateRequest request = new SeatCreateRequest(
                    "1A",
                    Type.STANDARD,
                    1L
            );

            Seat seat = seatMapper.toEntity(request);

            assertEquals(Type.STANDARD, seat.getType());
        }

        @Test
        @DisplayName("Debe actualizar solo el campo de tipo")
        void shouldUpdateOnlyTypeField() {
            Bus originalBus = Bus.builder()
                    .id(1L)
                    .plate("ORIGINAL")
                    .build();

            Seat existingSeat = Seat.builder()
                    .id(1L)
                    .number("ORIGINAL_NUMBER")
                    .type(Type.STANDARD)
                    .bus(originalBus)
                    .build();

            SeatUpdateRequest request = new SeatUpdateRequest(
                    Type.PREFERENTIAL
            );

            seatMapper.updateEntity(request, existingSeat);

            assertEquals(Type.PREFERENTIAL, existingSeat.getType());
            assertEquals("ORIGINAL_NUMBER", existingSeat.getNumber());
            assertEquals(originalBus, existingSeat.getBus());
            assertEquals(1L, existingSeat.getId());
        }

        @Test
        @DisplayName("Debe preservar todos los campos durante el ciclo completo de mapeo")
        void shouldPreserveAllFieldsDuringFullCycleMapping() {
            SeatCreateRequest request = new SeatCreateRequest(
                    "FULL-TEST-1",
                    Type.PREFERENTIAL,
                    999L
            );

            Seat seat = seatMapper.toEntity(request);
            seat.setId(100L);
            seat.setBus(Bus.builder()
                    .id(999L)
                    .plate("TEST-PLATE")
                    .capacity(50)
                    .statusBus(StatusBus.ACTIVE)
                    .build());

            SeatResponse response = seatMapper.toResponse(seat);

            assertEquals(100L, response.id());
            assertEquals("FULL-TEST-1", response.number());
            assertEquals("PREFERENTIAL", response.type());
            assertEquals(999L, response.busId());
            assertEquals("TEST-PLATE", response.busPlate());
            assertEquals(50, response.busCapacity());
        }

}