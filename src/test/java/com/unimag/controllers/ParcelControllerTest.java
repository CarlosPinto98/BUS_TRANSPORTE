package com.unimag.controllers;

import com.unimag.DTO.ParcelDTO;
import com.unimag.entities.Enums.StatusParcel;
import com.unimag.service.ParcelService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParcelControllerTest {

    @Mock
    private ParcelService parcelService;

    @InjectMocks
    private ParcelController parcelController;


    private ParcelDTO.parcelResponse mockResponse;

    @BeforeEach
    void setUp() {

        mockResponse = new ParcelDTO.parcelResponse(
                1L, "CODE123", "Sender", "1234567890",
                "Receiver", "0987654321", new BigDecimal("10.00"),
                "CREATED", null, null,
                LocalDateTime.now(), null, 1L, 2L, 10L
        );
    }

    @Test
    @DisplayName("🔍 GET /{id}: Debería retornar un paquete específico por ID (200 OK)")
    void getAll_shouldReturnPageOfParcelsAndOkStatus() {

        Pageable pageable = PageRequest.of(0, 10);
        List<ParcelDTO.parcelResponse> parcelList = Collections.singletonList(mockResponse);
        Page<ParcelDTO.parcelResponse> mockPage = new PageImpl<>(parcelList, pageable, 1);


        when(parcelService.getAll(pageable)).thenReturn(mockPage);
        ResponseEntity<Page<ParcelDTO.parcelResponse>> response = parcelController.getAll(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());

        verify(parcelService, times(1)).getAll(pageable);
    }

    @Test
    @DisplayName("🔍 GET /{id}: Debería retornar un paquete específico por ID (200 OK)")
    void getById_shouldReturnParcelResponseAndOkStatus() {
        Long parcelId = 1L;

        when(parcelService.get(parcelId)).thenReturn(mockResponse);

        ResponseEntity<ParcelDTO.parcelResponse> response = parcelController.get(parcelId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(parcelId, response.getBody().id());

        verify(parcelService, times(1)).get(parcelId);
    }

    @Test
    @DisplayName("🔍 GET /code/{code}: Debería retornar un paquete por código de rastreo ")
    void getByCode_shouldReturnParcelResponseAndOkStatus() {
        String parcelCode = "CODE123";

        when(parcelService.get(parcelCode)).thenReturn(mockResponse);

        ResponseEntity<ParcelDTO.parcelResponse> response = parcelController.get(parcelCode);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(parcelCode, response.getBody().code());

        verify(parcelService, times(1)).get(parcelCode);
    }

    @Test
    @DisplayName("✍️ PUT /update/{id}: Debería actualizar el estado del paquete y retornar el objeto actualizado")
    void update_shouldReturnUpdatedParcelAndOkStatus() {
        Long parcelId = 1L;

        ParcelDTO.parcelUpdateRequest request = new ParcelDTO.parcelUpdateRequest(
                StatusParcel.IN_TRANSIT, "http://photo.url", "1234");

        ParcelDTO.parcelResponse updatedResponse = new ParcelDTO.parcelResponse(
                parcelId, "CODE123", "Sender", "1234567890",
                "Receiver", "0987654321", new BigDecimal("10.00"),
                "IN_TRANSIT", "http://photo.url", "1234",
                LocalDateTime.now(), null, 1L, 2L, 10L
        );


        when(parcelService.update(request, parcelId)).thenReturn(updatedResponse);

        ResponseEntity<ParcelDTO.parcelResponse> response = parcelController.update(parcelId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("IN_TRANSIT", response.getBody().statusParcel());

        verify(parcelService, times(1)).update(request, parcelId);
    }

    @Test
    @DisplayName("🗑️ DELETE /delete/{id}: Debería llamar al servicio para eliminar y retornar 204 No Content")
    void delete_shouldCallServiceAndDeleteAndReturnNoContentStatus() {
        Long parcelId = 1L;

        when(parcelService.delete(parcelId)).thenReturn(true);

        ResponseEntity<Void> response = parcelController.delete(parcelId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(parcelService, times(1)).delete(parcelId);
    }
}