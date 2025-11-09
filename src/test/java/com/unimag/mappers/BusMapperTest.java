package com.unimag.mappers;

import com.unimag.DTO.BusDTO.*;
import com.unimag.entities.Bus;
import com.unimag.entities.Enums.StatusBus;
import com.unimag.entities.Enums.Type;
import com.unimag.entities.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BusMapper Tests")
class BusMapperTest {

    private BusMapper busMapper;

    @BeforeEach
    void setUp() {
        busMapper = Mappers.getMapper(BusMapper.class);
    }


    @Test
    @DisplayName("Debe mapear BusCreateRequest a la entidad Bus")
    void toEntity() {

        Map<String, Object> amenities = new HashMap<>();
        amenities.put("wifi", true);
        amenities.put("ac", true);
        amenities.put("tv", false);

        BusCreateRequest request = new BusCreateRequest(
                "ABC123",
                45,
                amenities,
                StatusBus.ACTIVE
        );

        Bus bus = busMapper.toEntity(request);

        assertNotNull(bus);
        assertEquals("ABC123", bus.getPlate());
        assertEquals(45, bus.getCapacity());
        assertEquals(StatusBus.ACTIVE, bus.getStatusBus());
        assertNotNull(bus.getAmenities());
        assertEquals(3, bus.getAmenities().size());
        assertTrue((Boolean) bus.getAmenities().get("wifi"));
        assertNull(bus.getId());
    }

    @Test
    @DisplayName("Debe actualizar la entidad Bus desde BusUpdateRequest")
    void updateEntity() {

        Map<String, Object> oldAmenities = new HashMap<>();
        oldAmenities.put("wifi", false);

        Bus existingBus = Bus.builder()
                .id(1L)
                .plate("OLD123")
                .capacity(40)
                .amenities(oldAmenities)
                .statusBus(StatusBus.ACTIVE)
                .build();

        Map<String, Object> newAmenities = new HashMap<>();
        newAmenities.put("wifi", true);
        newAmenities.put("bathroom", true);

        BusUpdateRequest request = new BusUpdateRequest(
                50,
                newAmenities,
                StatusBus.MAINTENANCE
        );

        busMapper.updateEntity(request, existingBus);

        assertEquals(50, existingBus.getCapacity());
        assertEquals(StatusBus.MAINTENANCE, existingBus.getStatusBus());
        assertEquals(2, existingBus.getAmenities().size());
        assertTrue((Boolean) existingBus.getAmenities().get("wifi"));
        assertEquals("OLD123", existingBus.getPlate());
    }

    @Test
    @DisplayName("Debe mapear la entidad Bus a BusResponse")
    void toResponse() {

        Map<String, Object> amenities = new HashMap<>();
        amenities.put("wifi", true);
        amenities.put("ac", true);

        Bus bus = Bus.builder()
                .id(1L)
                .plate("XYZ789")
                .capacity(40)
                .amenities(amenities)
                .statusBus(StatusBus.ACTIVE)
                .build();

        BusResponse response = busMapper.toResponse(bus);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("XYZ789", response.plate());
        assertEquals(40, response.capacity());
        assertEquals("ACTIVE", response.statusBus());
    }

    @Test
    @DisplayName("Debe mapear Bus con asientos a BusWithSeatsResponse")
    void toResponseWithSeats() {

        Bus bus = Bus.builder()
                .id(1L)
                .plate("ABC123")
                .capacity(45)
                .amenities(new HashMap<>())
                .statusBus(StatusBus.ACTIVE)
                .build();

        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= 45; i++) {
            seats.add(Seat.builder()
                    .id((long) i)
                    .number(String.valueOf(i))
                    .type(Type.STANDARD)
                    .bus(bus)
                    .build());
        }
        bus.setSeats(seats);

        Integer availableSeats = 30;

        BusWithSeatsResponse response = busMapper.toResponseWithSeats(bus, availableSeats);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("ABC123", response.plate());
        assertEquals(45, response.capacity());
        assertEquals("ACTIVE", response.statusBus());
        assertEquals(45, response.totalSeats());
        assertEquals(30, response.availableSeats());
    }

    @Test
    @DisplayName("Debe manejar un mapa de comodidades vacío")
    void shouldHandleEmptyAmenitiesMap() {
        BusCreateRequest request = new BusCreateRequest(
                "TEST123",
                40,
                new HashMap<>(),
                StatusBus.ACTIVE
        );

        Bus bus = busMapper.toEntity(request);

        assertNotNull(bus);
        assertNotNull(bus.getAmenities());
        assertTrue(bus.getAmenities().isEmpty());
    }

    @Test
    @DisplayName("Debe manejar un mapa de comodidades nulo")
    void shouldHandleNullAmenitiesMap() {
        BusCreateRequest request = new BusCreateRequest(
                "TEST123",
                40,
                null,
                StatusBus.ACTIVE
        );

        Bus bus = busMapper.toEntity(request);

        assertNotNull(bus);
    }

    @Test
    @DisplayName("Debe mapear todos los tipos de BusStatus correctamente")
    void shouldMapAllBusStatusTypes() {
        for (StatusBus status : StatusBus.values()) {
            BusCreateRequest request = new BusCreateRequest(
                    "TEST" + status,
                    40,
                    new HashMap<>(),
                    status
            );

            Bus bus = busMapper.toEntity(request);
            bus.setId(1L);
            BusResponse response = busMapper.toResponse(bus);

            assertEquals(status, bus.getStatusBus());
            assertEquals(status.name(), response.statusBus());
        }
    }

    @Test
    @DisplayName("Debe manejar estructuras de comodidades complejas")
    void shouldHandleComplexAmenitiesStructures() {
        Map<String, Object> amenities = new HashMap<>();
        amenities.put("wifi", true);
        amenities.put("ac", true);
        amenities.put("seats", Map.of("reclining", true, "leather", false));
        amenities.put("entertainment", List.of("tv", "radio", "movies"));
        amenities.put("capacity_details", Map.of("standard", 40, "preferential", 5));

        BusCreateRequest request = new BusCreateRequest(
                "LUXURY001",
                45,
                amenities,
                StatusBus.ACTIVE
        );

        Bus bus = busMapper.toEntity(request);

        assertNotNull(bus);
        assertNotNull(bus.getAmenities());
        assertEquals(5, bus.getAmenities().size());
        assertTrue((Boolean) bus.getAmenities().get("wifi"));
        assertNotNull(bus.getAmenities().get("seats"));
        assertNotNull(bus.getAmenities().get("entertainment"));
    }

    @Test
    @DisplayName("Debe manejar autobús sin asientos en BusWithSeatsResponse")
    void shouldHandleBusWithoutSeatsInWithSeatsResponse() {
        Bus bus = Bus.builder()
                .id(1L)
                .plate("EMPTY001")
                .capacity(45)
                .amenities(new HashMap<>())
                .statusBus(StatusBus.ACTIVE)
                .seats(new ArrayList<>())
                .build();

        Integer availableSeats = 45;

        BusWithSeatsResponse response = busMapper.toResponseWithSeats(bus, availableSeats);

        assertNotNull(response);
        assertEquals(0, response.totalSeats());
        assertEquals(45, response.availableSeats());
    }

    @Test
    @DisplayName("Debe manejar autobús nulo en BusWithSeatsResponse")
    void HandleNullBusInWithSeatsResponse() {
        BusWithSeatsResponse response = busMapper.toResponseWithSeats(null, 0);

        assertNull(response);
    }

    @Test
    @DisplayName("Debe preservar todos los campos durante el ciclo completo de mapeo")
    void PreserveAllFieldsDuringFullCycleMapping() {
        Map<String, Object> amenities = new HashMap<>();
        amenities.put("wifi", true);
        amenities.put("ac", false);

        BusCreateRequest request = new BusCreateRequest(
                "FULL123",
                42,
                amenities,
                StatusBus.MAINTENANCE
        );

        Bus bus = busMapper.toEntity(request);
        bus.setId(999L);
        BusResponse response = busMapper.toResponse(bus);

        assertEquals(999L, response.id());
        assertEquals("FULL123", response.plate());
        assertEquals(42, response.capacity());
        assertEquals("MAINTENANCE", response.statusBus());
    }
}