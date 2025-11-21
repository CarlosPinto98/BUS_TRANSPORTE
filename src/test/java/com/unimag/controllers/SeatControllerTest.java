package com.unimag.controllers;

import com.unimag.DTO.SeatDTO;
import com.unimag.entities.Enums.Type;
import com.unimag.service.SeatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("💺 Pruebas Unitarias del Controlador de Asientos (SeatController)")
class SeatControllerTest {

    @Mock
    private SeatService seatService;

    @InjectMocks
    private SeatController seatController;

    private SeatDTO.seatResponse response;
    private SeatDTO.seatCreateRequest createRequest;
    private SeatDTO.seatUpdateRequest updateRequest;

    private final Long SEAT_ID = 1L;
    private final Long BUS_ID = 10L;
    private final String SEAT_NUMBER = "05";

    @BeforeEach
    void setUp() {

        response = new SeatDTO.seatResponse(
                SEAT_ID,
                SEAT_NUMBER,
                Type.STANDARD.name(),
                BUS_ID,
                "ABC-123",
                40
        );


        createRequest = new SeatDTO.seatCreateRequest(
                SEAT_NUMBER,
                Type.STANDARD,
                BUS_ID
        );

        updateRequest = new SeatDTO.seatUpdateRequest(
                Type.PREFERENTIAL, // Cambiando el tipo de asiento
                BUS_ID,
                null // El número no es obligatorio en el update
        );
    }


    @Test
    @DisplayName("🔄 PUT /{id}: Debería actualizar el asiento y retornar")
    void update_shouldReturnUpdatedSeatAndOkStatus() {

        SeatDTO.seatResponse updatedResponse = new SeatDTO.seatResponse(
                SEAT_ID, SEAT_NUMBER, Type.PREFERENTIAL.name(), BUS_ID, "ABC-123", 40);

        when(seatService.update(updateRequest, SEAT_ID)).thenReturn(updatedResponse);

        ResponseEntity<SeatDTO.seatResponse> response = seatController.update(SEAT_ID, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Type.PREFERENTIAL.name(), response.getBody().type());
        verify(seatService, times(1)).update(updateRequest, SEAT_ID);
    }

    @Test
    @DisplayName("🗑️ DELETE /{id}: Debería eliminar el asiento y retornar ")
    void delete_shouldCallServiceAndDeleteAndReturnNoContentStatus() {

        doNothing().when(seatService).delete(SEAT_ID);

        ResponseEntity<Void> response = seatController.delete(SEAT_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(seatService, times(1)).delete(SEAT_ID);
    }

    @Test
    @DisplayName("✅ GET /all: Debería retornar una página de asientos con estado ")
    void getAll_shouldReturnPageOfSeatsAndOkStatus() {

        Pageable pageable = PageRequest.of(0, 10);
        List<SeatDTO.seatResponse> seatList = Collections.singletonList(response);
        Page<SeatDTO.seatResponse> mockPage = new PageImpl<>(seatList, pageable, 1);

        when(seatService.getAll(pageable)).thenReturn(mockPage);

        ResponseEntity<Page<SeatDTO.seatResponse>> response = seatController.getAll(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(seatService, times(1)).getAll(pageable);
    }

    @Test
    @DisplayName("🔍 GET /{id}: Debería retornar un asiento específico por ID ")
    void get_shouldReturnSeatResponseAndOkStatus() {

        when(seatService.get(SEAT_ID)).thenReturn(response);

        ResponseEntity<SeatDTO.seatResponse> response = seatController.get(SEAT_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SEAT_ID, response.getBody().id());
        verify(seatService, times(1)).get(SEAT_ID);
    }

    @Test
    @DisplayName("🔎 GET /bus/{busId}/number/{number}: Debería retornar un asiento por número y bus ID ")
    void getSeatByNumberAndBusId_shouldReturnSeatResponseAndOkStatus() {

        int numberInt = Integer.parseInt(SEAT_NUMBER);
        when(seatService.getSeatByNumberAndBusId(numberInt, BUS_ID)).thenReturn(response);

        ResponseEntity<SeatDTO.seatResponse> response = seatController.getSeatByNumberAndBusId(numberInt, BUS_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(BUS_ID, response.getBody().busId());
        assertEquals(SEAT_NUMBER, response.getBody().number());
        verify(seatService, times(1)).getSeatByNumberAndBusId(numberInt, BUS_ID);
    }

//    @Test
//    @DisplayName("➕ POST /create: Debería crear un nuevo asiento y retornar ")
//    void create_shouldReturnCreatedSeatAndCreatedStatus() {
//
//        when(seatService.save(createRequest)).thenReturn(response);
//
//        ResponseEntity<SeatDTO.seatResponse> response = seatController.create(createRequest);
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals(SEAT_NUMBER, response.getBody().number());
//        verify(seatService, times(1)).save(createRequest);
//    }
}

