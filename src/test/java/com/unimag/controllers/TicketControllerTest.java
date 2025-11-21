package com.unimag.controllers;

import com.unimag.DTO.TicketDTO;
import com.unimag.entities.Enums.PaymentMethod;
import com.unimag.entities.Enums.StatusTicket;
import com.unimag.security.service.CustomUserDetails;
import com.unimag.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("🎫 Pruebas Unitarias del Controlador de Tiquetes (TicketController)")
class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private TicketController ticketController;

    @Mock
    private Authentication authentication;

    private TicketDTO.ticketResponse response;
    private TicketDTO.ticketCreateRequest createRequest;
    private TicketDTO.ticketUpdateRequest updateRequest;
    private CustomUserDetails userDetails;

    private final Long TICKET_ID = 1L;
    private final Long PASSENGER_ID = 10L;
    private final Long TRIP_ID = 5L;
    private final String SEAT_NUMBER = "A05";
    private final String QR_CODE = "XYZ123";

    @BeforeEach
    void setUp() {

        userDetails = new CustomUserDetails(
                PASSENGER_ID,
                "passenger@mail.com",
                "pass",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_PASSENGER"))
        );
        when(authentication.getPrincipal()).thenReturn(userDetails);

        response = new TicketDTO.ticketResponse(
                TICKET_ID, TRIP_ID, "2025-11-25", "08:00",
                PASSENGER_ID, "Carlos Pinto",
                1L, "Stop A", 2L, "Stop B",
                SEAT_NUMBER,
                new BigDecimal("50.00"),
                PaymentMethod.CASH.name(),
                StatusTicket.SOLD.name(),
                QR_CODE,
                LocalDateTime.now()
        );

        createRequest = new TicketDTO.ticketCreateRequest(
                TRIP_ID, PASSENGER_ID, 1L, 2L, SEAT_NUMBER, PaymentMethod.CASH
        );

        updateRequest = new TicketDTO.ticketUpdateRequest(
                StatusTicket.CANCELLED
        );
    }

    @Test
    @DisplayName("➕ POST /create: Debería crear un tiquete y retornar 201 CREATED")
    void createTicket_shouldReturnCreatedTicketAndCreatedStatus() {

        when(ticketService.createTicket(createRequest)).thenReturn(response);

        ResponseEntity<TicketDTO.ticketResponse> response = ticketController.createTicket(createRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(ticketService, times(1)).createTicket(createRequest);
    }

    @Test
    @DisplayName("❌ POST /{id}/cancel: Debería cancelar el tiquete y retornar 200 OK")
    void cancelTicket_shouldReturnCancelledTicket() {

        TicketDTO.ticketResponse cancelledResponse = new TicketDTO.ticketResponse(
                TICKET_ID, TRIP_ID, "2025-11-25", "08:00", PASSENGER_ID, "Carlos Pinto",
                1L, "Stop A", 2L, "Stop B", SEAT_NUMBER, new BigDecimal("50.00"),
                PaymentMethod.CASH.name(), StatusTicket.CANCELLED.name(), QR_CODE, LocalDateTime.now()
        );
        when(ticketService.cancelTicket(TICKET_ID)).thenReturn(cancelledResponse);

        ResponseEntity<TicketDTO.ticketResponse> response = ticketController.cancelTicket(TICKET_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(StatusTicket.CANCELLED.name(), response.getBody().statusTicket());
        verify(ticketService, times(1)).cancelTicket(TICKET_ID);
    }

    @Test
    @DisplayName("🔄 PUT /update/{id}: Debería actualizar el tiquete y retornar 200 OK")
    void updateTicket_shouldReturnUpdatedTicket() {

        when(ticketService.updateTicket(TICKET_ID, updateRequest)).thenReturn(response);

        ResponseEntity<TicketDTO.ticketResponse> response = ticketController.updateTicket(TICKET_ID, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(ticketService, times(1)).updateTicket(TICKET_ID, updateRequest);
    }

    @Test
    @DisplayName("🗑️ DELETE /delete/{id}: Debería eliminar el tiquete y retornar 204 NO CONTENT")
    void deleteTicket_shouldReturnNoContent() {

        doNothing().when(ticketService).deleteTicket(TICKET_ID);

        ResponseEntity<Void> response = ticketController.deleteTicket(TICKET_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(ticketService, times(1)).deleteTicket(TICKET_ID);
    }

    @Test
    @DisplayName("✅ POST /{id}/used: Debería marcar el tiquete como USADO y retornar 200 OK")
    void markAsUsed_shouldReturnUpdatedTicket() {

        TicketDTO.ticketResponse usedResponse = new TicketDTO.ticketResponse(
                TICKET_ID, TRIP_ID, "2025-11-25", "08:00", PASSENGER_ID, "Carlos Pinto",
                1L, "Stop A", 2L, "Stop B", SEAT_NUMBER, new BigDecimal("50.00"),
                PaymentMethod.CASH.name(), StatusTicket.USED.name(), QR_CODE, LocalDateTime.now()
        );
        when(ticketService.markAsUsed(TICKET_ID)).thenReturn(usedResponse);

        ResponseEntity<TicketDTO.ticketResponse> response = ticketController.markAsUsed(TICKET_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(StatusTicket.USED.name(), response.getBody().statusTicket());
        verify(ticketService, times(1)).markAsUsed(TICKET_ID);
    }

    @Test
    @DisplayName("⛔ POST /{id}/no-show: Debería marcar el tiquete como NO SHOW y retornar 200 OK")
    void markAsNoShow_shouldReturnUpdatedTicket() {

        TicketDTO.ticketResponse noShowResponse = new TicketDTO.ticketResponse(
                TICKET_ID, TRIP_ID, "2025-11-25", "08:00", PASSENGER_ID, "Carlos Pinto",
                1L, "Stop A", 2L, "Stop B", SEAT_NUMBER, new BigDecimal("50.00"),
                PaymentMethod.CASH.name(), StatusTicket.NO_SHOW.name(), QR_CODE, LocalDateTime.now()
        );
        when(ticketService.markAsNoShow(TICKET_ID)).thenReturn(noShowResponse);

        ResponseEntity<TicketDTO.ticketResponse> response = ticketController.markAsNoShow(TICKET_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(StatusTicket.NO_SHOW.name(), response.getBody().statusTicket());
        verify(ticketService, times(1)).markAsNoShow(TICKET_ID);
    }

    @Test
    @DisplayName("❓ GET /check-availability: Debería verificar si un asiento está disponible ")
    void isSeatAvailable_shouldReturnBooleanStatus() {

        when(ticketService.isSeatAvailable(TRIP_ID, SEAT_NUMBER)).thenReturn(true);

        ResponseEntity<Boolean> response = ticketController.isSeatAvailable(TRIP_ID, SEAT_NUMBER);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(ticketService, times(1)).isSeatAvailable(TRIP_ID, SEAT_NUMBER);
    }

    @Test
    @DisplayName("🔢 GET /trip/{tripId}/count: Debería contar los tiquetes vendidos para un viaje ")
    void countSoldTicketsByTrip_shouldReturnCount() {

        long mockCount = 25L;
        when(ticketService.countSoldTicketsByTrip(TRIP_ID)).thenReturn(mockCount);

        ResponseEntity<Long> response = ticketController.countSoldTicketsByTrip(TRIP_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockCount, response.getBody());
        verify(ticketService, times(1)).countSoldTicketsByTrip(TRIP_ID);
    }

    @Test
    @DisplayName("👤 GET /my-tickets: Debería retornar los tiquetes del usuario autenticado ")
    void getMyTickets_shouldReturnTicketsForAuthenticatedUser() {

        List<TicketDTO.ticketResponse> mockList = Collections.singletonList(response);
        when(ticketService.getTicketsByPassengerId(PASSENGER_ID)).thenReturn(mockList);

        ResponseEntity<List<TicketDTO.ticketResponse>> response = ticketController.getMyTickets(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(ticketService, times(1)).getTicketsByPassengerId(PASSENGER_ID);
    }

    @Test
    @DisplayName("📋 GET /all: Debería retornar la lista de todos los tiquetes ")
    void getAllTickets_shouldReturnListOfAllTickets() {

        List<TicketDTO.ticketResponse> mockList = Collections.singletonList(response);
        when(ticketService.getAllTickets()).thenReturn(mockList);

        ResponseEntity<List<TicketDTO.ticketResponse>> response = ticketController.getAllTickets();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(ticketService, times(1)).getAllTickets();
    }

    @Test
    @DisplayName("🔍 GET /{id}: Debería retornar un tiquete por ID ")
    void getTicketById_shouldReturnSpecificTicket() {

        when(ticketService.getTicketById(TICKET_ID)).thenReturn(response);

        ResponseEntity<TicketDTO.ticketResponse> response = ticketController.getTicketById(TICKET_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(TICKET_ID, response.getBody().id());
        verify(ticketService, times(1)).getTicketById(TICKET_ID);
    }

    @Test
    @DisplayName("⚙️ GET /{id}/details: Debería retornar un tiquete con detalles completos ")
    void getTicketWithDetails_shouldReturnDetailedTicket() {

        when(ticketService.getTicketWithDetails(TICKET_ID)).thenReturn(response);

        ResponseEntity<TicketDTO.ticketResponse> response = ticketController.getTicketWithDetails(TICKET_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(ticketService, times(1)).getTicketWithDetails(TICKET_ID);
    }

    @Test
    @DisplayName("🖼️ GET /qr/{qrCode}: Debería retornar un tiquete por código QR ")
    void getTicketByQrCode_shouldReturnTicketByQrCode() {

        when(ticketService.getTicketByQrCode(QR_CODE)).thenReturn(response);

        ResponseEntity<TicketDTO.ticketResponse> response = ticketController.getTicketByQrCode(QR_CODE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(QR_CODE, response.getBody().qrCode());
        verify(ticketService, times(1)).getTicketByQrCode(QR_CODE);
    }

    @Test
    @DisplayName("🚌 GET /trip/{tripId}: Debería retornar tiquetes por ID de viaje ")
    void getTicketsByTrip_shouldReturnTicketsForTrip() {

        List<TicketDTO.ticketResponse> mockList = Collections.singletonList(response);
        when(ticketService.getTicketsByTripId(TRIP_ID)).thenReturn(mockList);

        ResponseEntity<List<TicketDTO.ticketResponse>> response = ticketController.getTicketsByTrip(TRIP_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(ticketService, times(1)).getTicketsByTripId(TRIP_ID);
    }

    @Test
    @DisplayName("🚦 GET /trip/{tripId}/status/{status}: Debería retornar tiquetes por viaje y estado ")
    void getTicketsByTripAndStatus_shouldReturnFilteredTickets() {

        StatusTicket status = StatusTicket.SOLD;
        List<TicketDTO.ticketResponse> mockList = Collections.singletonList(response);
        when(ticketService.getTicketsByTripAndStatus(TRIP_ID, status)).thenReturn(mockList);

        ResponseEntity<List<TicketDTO.ticketResponse>> response = ticketController.getTicketsByTripAndStatus(TRIP_ID, status);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(ticketService, times(1)).getTicketsByTripAndStatus(TRIP_ID, status);
    }

    @Test
    @DisplayName("🧍 GET /passenger/{passengerId}: Debería retornar tiquetes por ID de pasajero ")
    void getTicketsByPassenger_shouldReturnTicketsForPassengerId() {

        List<TicketDTO.ticketResponse> mockList = Collections.singletonList(response);
        when(ticketService.getTicketsByPassengerId(PASSENGER_ID)).thenReturn(mockList);

        ResponseEntity<List<TicketDTO.ticketResponse>> response = ticketController.getTicketsByPassenger(PASSENGER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(ticketService, times(1)).getTicketsByPassengerId(PASSENGER_ID);
    }
}

