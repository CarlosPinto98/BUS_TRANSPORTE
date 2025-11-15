package com.unimag.service;

import com.unimag.DTO.TicketDTO;
import com.unimag.entities.*;
import com.unimag.entities.Enums.PaymentMethod;
import com.unimag.entities.Enums.StatusSeatHold;
import com.unimag.entities.Enums.StatusTicket;
import com.unimag.mappers.TicketMapper;
import com.unimag.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StopRepository stopRepository;

    @Mock
    private FareRuleRepository fareRuleRepository;

    @Mock
    private SeatHoldRepository seatHoldRepository;

    @Spy
    private TicketMapper ticketMapper = Mappers.getMapper(TicketMapper.class);

    @InjectMocks
    private TicketServiceImpl ticketService;

    private Ticket ticket;
    private Trip trip;
    private User passenger;
    private Stop fromStop;
    private Stop toStop;
    private TicketDTO.ticketCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        fromStop = Stop.builder().id(1L).name("Bogotá").order(0).build();
        toStop = Stop.builder().id(2L).name("Tunja").order(1).build();
        trip = Trip.builder()
                .id(1L)
                .date(LocalDate.now())
                .departureAt(LocalDateTime.now())
                .route(Route.builder().id(1L).build())
                .build();
        passenger = User.builder()
                .id(1L)
                .name("John Doe")
                .build();
        ticket = Ticket.builder()
                .id(1L)
                .trip(trip)
                .passenger(passenger)
                .fromStop(fromStop)
                .toStop(toStop)
                .seatNumber("1A")
                .price(new BigDecimal("50000"))
                .paymentMethod(PaymentMethod.CASH)
                .statusTicket(StatusTicket.SOLD)
                .qrCode("QR-123")
                .createdAt(LocalDateTime.now())
                .build();
        createRequest = new TicketDTO.ticketCreateRequest(
                1L, 1L, 1L, 2L, "1A", PaymentMethod.CASH
        );
    }


    @Test
    @DisplayName("Debe crear un ticket exitosamente")
    void createTicket() {

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(userRepository.findById(1L)).thenReturn(Optional.of(passenger));
        when(stopRepository.findById(1L)).thenReturn(Optional.of(fromStop));
        when(stopRepository.findById(2L)).thenReturn(Optional.of(toStop));
        when(ticketRepository.findSoldTicketBySeat(any(), any())).thenReturn(Optional.empty());
        when(seatHoldRepository.existsByTripIdAndSeatNumberAndStatusSeatHold(any(), any(), any()))
                .thenReturn(false);
        when(fareRuleRepository.findFareForSegment(any(), any(), any()))
                .thenReturn(Optional.of(FareRule.builder()
                        .basePrice(new BigDecimal("50000")).build()));
        when(ticketRepository.save(any())).thenReturn(ticket);

        TicketDTO.ticketResponse result = ticketService.createTicket(createRequest);

        assertNotNull(result);
        assertEquals("1A", result.seatNumber());
        verify(ticketRepository).save(any(Ticket.class));
        verify(ticketMapper).toEntity(createRequest);
        verify(ticketMapper).toResponse(any(Ticket.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el seat no está disponible")
    void ThrowExceptionWhenSeatNotAvailable() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(userRepository.findById(1L)).thenReturn(Optional.of(passenger));
        when(stopRepository.findById(1L)).thenReturn(Optional.of(fromStop));
        when(stopRepository.findById(2L)).thenReturn(Optional.of(toStop));
        when(ticketRepository.findSoldTicketBySeat(any(), any()))
                .thenReturn(Optional.of(ticket));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.createTicket(createRequest)
        );

        assertTrue(exception.getMessage().contains("not available"));
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void updateTicket() {
    }

    @Test
    void getTicketById() {
    }

    @Test
    void getTicketByQrCode() {
    }

    @Test
    void getTicketWithDetails() {
    }

    @Test
    void getAllTickets() {
    }

    @Test
    void getTicketsByTripId() {
    }

    @Test
    void getTicketsByPassengerId() {
    }

    @Test
    void getTicketsByTripAndStatus() {
    }

    @Test
    void deleteTicket() {
    }

    @Test
    @DisplayName("Debe cancelar un ticket")
    void cancelTicket() {

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any())).thenReturn(ticket);

        TicketDTO.ticketResponse result = ticketService.cancelTicket(1L);

        assertNotNull(result);
        assertEquals(StatusTicket.CANCELLED, ticket.getStatusTicket());
        verify(ticketRepository).save(ticket);
    }

    @Test
    @DisplayName("Debe marcar ticket como no_show")
    void markAsNoShow() {

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any())).thenReturn(ticket);

        TicketDTO.ticketResponse result = ticketService.markAsNoShow(1L);

        assertNotNull(result);
        assertEquals(StatusTicket.NO_SHOW, ticket.getStatusTicket());
    }

    @Test
    void markAsUsed() {
    }

    @Test
    @DisplayName("Debe verificar si el seat está disponible")
    void isSeatAvailable() {

        when(ticketRepository.findSoldTicketBySeat(1L, "1A")).thenReturn(Optional.empty());
        when(seatHoldRepository.existsByTripIdAndSeatNumberAndStatusSeatHold(
                1L, "1A", StatusSeatHold.HOLD)).thenReturn(false);

        boolean result = ticketService.isSeatAvailable(1L, "1A");

        assertTrue(result);
    }

    @Test
    void countSoldTicketsByTrip() {
    }

    @Test
    void getObject() {
    }

    @Test
    @DisplayName("Debe retornar false cuando seat está vendido")
    void ReturnFalseWhenSeatIsSold() {
        when(ticketRepository.findSoldTicketBySeat(1L, "1A"))
                .thenReturn(Optional.of(ticket));

        boolean result = ticketService.isSeatAvailable(1L, "1A");
        assertFalse(result);
    }
}