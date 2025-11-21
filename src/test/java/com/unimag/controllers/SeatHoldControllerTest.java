package com.unimag.controllers;

import com.unimag.DTO.SeatHoldDTO;
import com.unimag.entities.Enums.StatusSeatHold;
import com.unimag.security.service.CustomUserDetails;
import com.unimag.service.SeatHoldService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("🔒 Pruebas Unitarias del Controlador de Retención de Asientos (SeatHoldController)")
class SeatHoldControllerTest {

    @Mock
    private SeatHoldService seatHoldService;

    @InjectMocks
    private SeatHoldController seatHoldController;

    @Mock
    private Authentication authentication;

    private SeatHoldDTO.seatHoldResponse response;
    private SeatHoldDTO.seatHoldCreateRequest createRequest;
    private SeatHoldDTO.seatHoldUpdateRequest updateRequest;
    private CustomUserDetails userDetails;

    private final Long HOLD_ID = 5L;
    private final Long USER_ID = 100L;
    private final Long TRIP_ID = 20L;
    private final String SEAT_NUMBER = "08";

    @BeforeEach
    void setUp() {

        userDetails = new CustomUserDetails(USER_ID,
                "test@unimag.com",
                "password",
                Collections.emptyList());


        response = new SeatHoldDTO.seatHoldResponse(
                HOLD_ID, SEAT_NUMBER, LocalDateTime.now().plusMinutes(15),
                "HOLD", LocalDateTime.now(), TRIP_ID, USER_ID,
                "2025-12-01", "10:00", "Ruta A", 15
        );


        createRequest = new SeatHoldDTO.seatHoldCreateRequest(
                TRIP_ID, SEAT_NUMBER, 1L, 5L
        );


        updateRequest = new SeatHoldDTO.seatHoldUpdateRequest(
                StatusSeatHold.EXPIRED,
                USER_ID, TRIP_ID, 1L
        );


        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    @Test
    @DisplayName("➕ POST /create: Debería crear una retención de asiento y retornar ")
    void create_shouldReturnCreatedHoldAndCreatedStatus() {

        when(seatHoldService.create(createRequest, USER_ID)).thenReturn(response);

        ResponseEntity<SeatHoldDTO.seatHoldResponse> response = seatHoldController.create(authentication, createRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(this.response, response.getBody());
        verify(seatHoldService, times(1)).create(createRequest, USER_ID);
    }

    @Test
    @DisplayName("👤 GET /my-holds: Debería retornar las retenciones del usuario autenticado ")
    void getMyHolds_shouldReturnHoldsForAuthenticatedUser() {

        List<SeatHoldDTO.seatHoldResponse> mockList = Collections.singletonList(response);
        when(seatHoldService.getSeatHoldsByUserId(USER_ID)).thenReturn(mockList);

        ResponseEntity<List<SeatHoldDTO.seatHoldResponse>> response = seatHoldController.getMyHolds(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(seatHoldService, times(1)).getSeatHoldsByUserId(USER_ID);
    }

    @Test
    @DisplayName("🔓 DELETE /{id}/release: Debería liberar la retención y retornar")
    void releaseSeatHold_shouldCallServiceAndReturnNoContent() {

        doNothing().when(seatHoldService).releaseSeatHold(HOLD_ID);

        ResponseEntity<Void> response = seatHoldController.releaseSeatHold(HOLD_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(seatHoldService, times(1)).releaseSeatHold(HOLD_ID);
    }

    @Test
    @DisplayName("✅ GET /all: Debería retornar una lista paginada de todas las retenciones ")
    void getAll_shouldReturnListOfAllHolds() {

        Pageable pageable = Pageable.unpaged(); // Simplificación para pruebas
        List<SeatHoldDTO.seatHoldResponse> mockList = Collections.singletonList(response);
        when(seatHoldService.getAll(pageable)).thenReturn(mockList);

        ResponseEntity<List<SeatHoldDTO.seatHoldResponse>> response = seatHoldController.getAll(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(seatHoldService, times(1)).getAll(pageable);
    }

    @Test
    @DisplayName("🔍 GET /{id}: Debería retornar una retención por ID ")
    void getSeatHoldById_shouldReturnSpecificHold() {

        when(seatHoldService.getSeatHoldById(HOLD_ID)).thenReturn(response);

        ResponseEntity<SeatHoldDTO.seatHoldResponse> response = seatHoldController.getSeatHoldById(HOLD_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(HOLD_ID, response.getBody().id());
        verify(seatHoldService, times(1)).getSeatHoldById(HOLD_ID);
    }

    @Test
    @DisplayName("🚌 GET /trip/{tripId}: Debería retornar retenciones por ID de viaje")
    void getSeatHoldsByTrip_shouldReturnHoldsForSpecificTrip() {

        List<SeatHoldDTO.seatHoldResponse> mockList = Collections.singletonList(response);
        when(seatHoldService.getSeatHoldsByTripId(TRIP_ID)).thenReturn(mockList);

        ResponseEntity<List<SeatHoldDTO.seatHoldResponse>> response = seatHoldController.getSeatHoldsByTrip(TRIP_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(seatHoldService, times(1)).getSeatHoldsByTripId(TRIP_ID);
    }

    @Test
    @DisplayName("🟢 GET /trip/{tripId}/active: Debería retornar solo retenciones activas por ID de viaje ")
    void getActiveSeatHoldsByTrip_shouldReturnActiveHolds() {

        List<SeatHoldDTO.seatHoldResponse> mockList = Collections.singletonList(response);
        when(seatHoldService.getActiveSeatHoldsByTrip(TRIP_ID)).thenReturn(mockList);

        ResponseEntity<List<SeatHoldDTO.seatHoldResponse>> response = seatHoldController.getActiveSeatHoldsByTrip(TRIP_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(seatHoldService, times(1)).getActiveSeatHoldsByTrip(TRIP_ID);
    }

    @Test
    @DisplayName("👥 GET /user/{userId}: Debería retornar retenciones por ID de usuario ")
    void getSeatHoldsByUser_shouldReturnHoldsForSpecificUser() {

        List<SeatHoldDTO.seatHoldResponse> mockList = Collections.singletonList(response);
        when(seatHoldService.getSeatHoldsByUserId(USER_ID)).thenReturn(mockList);

        ResponseEntity<List<SeatHoldDTO.seatHoldResponse>> response = seatHoldController.getSeatHoldsByUser(USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(seatHoldService, times(1)).getSeatHoldsByUserId(USER_ID);
    }

    @Test
    @DisplayName("🔄 PUT /update/{id}: Debería actualizar la retención y retornar ")
    void update_shouldReturnUpdatedHold() {

        SeatHoldDTO.seatHoldResponse updatedResponse = new SeatHoldDTO.seatHoldResponse(
                HOLD_ID, SEAT_NUMBER, LocalDateTime.now().plusMinutes(5),
                "CANCELED", LocalDateTime.now(), TRIP_ID, USER_ID,
                "2025-12-01", "10:00", "Ruta A", 5
        );
        when(seatHoldService.update(updateRequest, HOLD_ID)).thenReturn(updatedResponse);

        ResponseEntity<SeatHoldDTO.seatHoldResponse> response = seatHoldController.update(HOLD_ID, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("CANCELED", response.getBody().statusSeatHold());
        verify(seatHoldService, times(1)).update(updateRequest, HOLD_ID);
    }

    @Test
    @DisplayName("🗑️ DELETE /delete/{id}: Debería eliminar la retención y retornar ")
    void deleteSeatHold_shouldCallServiceAndDeleteAndReturnNoContent() {

        doNothing().when(seatHoldService).delete(HOLD_ID);

        ResponseEntity<Void> response = seatHoldController.deleteSeatHold(HOLD_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(seatHoldService, times(1)).delete(HOLD_ID);
    }

    @Test
    @DisplayName("🎫 POST /{id}/convert: Debería convertir la retención a ticket y retornar ")
    void convertHoldToTicket_shouldCallServiceAndReturnOk() {

        doNothing().when(seatHoldService).convertHoldToTicket(HOLD_ID);

        ResponseEntity<Void> response = seatHoldController.convertHoldToTicket(HOLD_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(seatHoldService, times(1)).convertHoldToTicket(HOLD_ID);
    }

    @Test
    @DisplayName("❓ GET /check: Debería verificar si un asiento está retenido y retornar 'true'")
    void isSeatHeld_shouldReturnBooleanStatus() {

        when(seatHoldService.isSeatHeld(TRIP_ID, SEAT_NUMBER)).thenReturn(true);

        ResponseEntity<Boolean> response = seatHoldController.isSeatHeld(TRIP_ID, SEAT_NUMBER);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(seatHoldService, times(1)).isSeatHeld(TRIP_ID, SEAT_NUMBER);
    }
}
