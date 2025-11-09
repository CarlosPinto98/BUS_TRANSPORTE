package com.unimag.mappers;

import com.unimag.DTO.BaggageDTO.*;
import com.unimag.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("BaggageMapper Tests")
class BaggageMapperTest {

    private BaggageMapper baggageMapper;

    @BeforeEach
    void setUp() {
        baggageMapper = Mappers.getMapper(BaggageMapper.class);
    }

    @Test
    @DisplayName("Debe mapear BaggageCreateRequest a la entidad Baggage")
    void toEntity() {

        BaggageCreateRequest request = new BaggageCreateRequest(
                1L,
                new BigDecimal("25.50")
        );

        Baggage baggage = baggageMapper.toEntity(request);

        assertNotNull(baggage);
        assertEquals(new BigDecimal("25.50"), baggage.getWeightKg());
        assertNotNull(baggage.getTagCode());
        assertTrue(baggage.getTagCode().startsWith("BAG-"));

        // CORRECCIÓN 1: Se valida que el fee se haya calculado (11000.00 para 25.50kg)
        assertEquals(new BigDecimal("11000.00"), baggage.getFee());
        assertNotNull(baggage.getFee());
    }


    @Test
    @DisplayName("Debe calcular la tarifa correctamente para peso inferior a 20kg")
    void CalculateFeeForWeightUnder20Kg() {
        BaggageCreateRequest request = new BaggageCreateRequest(1L, new BigDecimal("15.0"));

        Baggage baggage = baggageMapper.toEntity(request);

        assertEquals(BigDecimal.ZERO, baggage.getFee());
    }

    @Test
    @DisplayName("Debe mapear la entidad Baggage a BaggageResponse")
    void toResponse() {

        Trip trip = Trip.builder().id(1L)
                .date(LocalDate.of(2025, 12, 25))
                .route(Route.builder().id(1L).origin("A").destination("B").build())
                .build();

        Ticket ticket = Ticket.builder().id(1L)
                .passenger(User.builder().id(1L).name("John Doe").build())
                .trip(trip)
                .build();

        Baggage baggage = Baggage.builder()
                .id(1L)
                .weightKg(new BigDecimal("30.00"))
                .fee(new BigDecimal("20000.00"))
                .tagCode("BAG-12345")
                .ticket(ticket)
                .build();

        BaggageResponse response = baggageMapper.toResponse(baggage);

        assertNotNull(response);
        assertEquals(1L, response.id());

        assertEquals(new BigDecimal("30.00"), response.weightKg());

        assertEquals(new BigDecimal("20000.00"), response.fee());

        assertEquals("BAG-12345", response.tagCode());
        assertEquals(1L, response.ticketId());
        assertEquals("John Doe", response.passengerName());
        assertNotNull(response.tripInfo());
    }

    @Test
    @DisplayName("Debe calcular la tarifa correctamente para peso superior a 20kg")
    void CalculateFeeForWeightOver20Kg() {
        BaggageCreateRequest request = new BaggageCreateRequest(1L, new BigDecimal("25.0"));

        Baggage baggage = baggageMapper.toEntity(request);

        assertEquals(new BigDecimal("10000.0"), baggage.getFee());
    }
}

