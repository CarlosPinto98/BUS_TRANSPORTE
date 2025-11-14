package com.unimag.service;

import com.unimag.DTO.ParcelDTO;
import com.unimag.entities.Parcel;
import com.unimag.entities.Route;
import com.unimag.entities.Stop;
import com.unimag.entities.Trip;
import com.unimag.entities.Enums.StatusParcel;
import com.unimag.entities.Enums.StatusTrip;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.ParcelMapper;
import com.unimag.repository.ParcelRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParcelServiceImplTest {

    @Mock
    private ParcelRepository parcelRepository;

    @Mock
    private ParcelMapper parcelMapper;

    @InjectMocks
    private ParcelServiceImpl parcelService;

    private Parcel parcel;
    private Stop fromStop;
    private Stop toStop;
    private Trip trip;
    private Route route;
    private ParcelDTO.parcelCreateRequest createRequest;
    private ParcelDTO.parcelUpdateRequest updateRequest;
    private ParcelDTO.parcelResponse response;

    @BeforeEach
    void setUp() {
        // Setup Route
        route = Route.builder()
                .id(1L)
                .code("RT-001")
                .name("Ruta Santa Marta - Barranquilla")
                .origin("Santa Marta")
                .destination("Barranquilla")
                .distanceKm(100)
                .durationMin(120)
                .build();

        // Setup Stops
        fromStop = Stop.builder()
                .id(1L)
                .name("Terminal Santa Marta")
                .order(1)
                .lat(BigDecimal.valueOf(11.2408))
                .lng(BigDecimal.valueOf(-74.2120))
                .route(route)
                .build();

        toStop = Stop.builder()
                .id(2L)
                .name("Terminal Barranquilla")
                .order(5)
                .lat(BigDecimal.valueOf(10.9685))
                .lng(BigDecimal.valueOf(-74.7813))
                .route(route)
                .build();

        // Setup Trip
        trip = Trip.builder()
                .id(1L)
                .date(LocalDate.now())
                .departureAt(LocalDateTime.now().plusHours(2))
                .arrivalEta(LocalDateTime.now().plusHours(4))
                .route(route)
                .statusTrip(StatusTrip.SCHEDULED)
                .build();

        // Setup Parcel
        parcel = Parcel.builder()
                .id(1L)
                .code("PCL-1234567890")
                .senderName("Juan Perez")
                .senderPhone("3001234567")
                .receiverName("Maria Lopez")
                .receiverPhone("3009876543")
                .price(BigDecimal.valueOf(25000))
                .statusParcel(StatusParcel.CREATED)
                .deliveryOtp("123456")
                .createdAt(LocalDateTime.now())
                .fromStop(fromStop)
                .toStop(toStop)
                .trip(trip)
                .build();

        // Setup DTOs
        createRequest = new ParcelDTO.parcelCreateRequest(
                "Juan Perez",           // senderName
                "3001234567",          // senderPhone
                "Maria Lopez",          // receiverName
                "3009876543",          // receiverPhone
                BigDecimal.valueOf(25000), // price
                1L,                     // fromStopId
                2L,                     // toStopId
                1L                      // tripId
        );

        updateRequest = new ParcelDTO.parcelUpdateRequest(
                StatusParcel.IN_TRANSIT, // statusParcel
                "https://example.com/proof.jpg", // proofPhotoUrl
                "123456"                // deliveryOtp
        );

        response = new ParcelDTO.parcelResponse(
                1L,
                "PCL-1234567890",
                "Juan Perez",
                "3001234567",
                "Maria Lopez",
                "3009876543",
                BigDecimal.valueOf(25000),
                "CREATED",
                null,
                "123456",
                LocalDateTime.now(),
                null,
                1L,
                2L,
                1L
        );
    }

    @Test
    @DisplayName("Save - Debe guardar un paquete exitosamente")
    void save_ShouldSaveParcelSuccessfully() {
        // Arrange
        when(parcelMapper.toEntity(createRequest)).thenReturn(parcel);
        when(parcelRepository.save(any(Parcel.class))).thenReturn(parcel);
        when(parcelMapper.toResponse(parcel)).thenReturn(response);

        // Act
        ParcelDTO.parcelResponse result = parcelService.save(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(response.id(), result.id());
        assertEquals(response.code(), result.code());
        assertEquals(response.senderName(), result.senderName());
        assertEquals(response.receiverName(), result.receiverName());

        verify(parcelMapper).toEntity(createRequest);
        verify(parcelRepository).save(parcel);
        verify(parcelMapper).toResponse(parcel);
    }

    @Test
    @DisplayName("Save - Debe generar código automáticamente")
    void save_ShouldGenerateCodeAutomatically() {
        // Arrange
        when(parcelMapper.toEntity(createRequest)).thenReturn(parcel);
        when(parcelRepository.save(any(Parcel.class))).thenReturn(parcel);
        when(parcelMapper.toResponse(parcel)).thenReturn(response);

        // Act
        ParcelDTO.parcelResponse result = parcelService.save(createRequest);

        // Assert
        assertNotNull(result);
        assertNotNull(result.code());
        assertTrue(result.code().startsWith("PCL-"));

        verify(parcelRepository).save(parcel);
    }

    @Test
    @DisplayName("Save - Debe generar OTP de 6 dígitos automáticamente")
    void save_ShouldGenerateOtpAutomatically() {
        // Arrange
        when(parcelMapper.toEntity(createRequest)).thenReturn(parcel);
        when(parcelRepository.save(any(Parcel.class))).thenReturn(parcel);
        when(parcelMapper.toResponse(parcel)).thenReturn(response);

        // Act
        ParcelDTO.parcelResponse result = parcelService.save(createRequest);

        // Assert
        assertNotNull(result);
        assertNotNull(result.deliveryOtp());
        assertEquals(6, result.deliveryOtp().length());

        verify(parcelRepository).save(parcel);
    }

    @Test
    @DisplayName("Save - Debe guardar paquete sin tripId (opcional)")
    void save_ShouldSaveParcelWithoutTrip() {
        // Arrange
        ParcelDTO.parcelCreateRequest requestWithoutTrip =
                new ParcelDTO.parcelCreateRequest(
                        "Juan Perez", "3001234567", "Maria Lopez", "3009876543",
                        BigDecimal.valueOf(25000), 1L, 2L, null
                );

        Parcel parcelWithoutTrip = Parcel.builder()
                .id(2L)
                .code("PCL-9876543210")
                .senderName("Juan Perez")
                .senderPhone("3001234567")
                .receiverName("Maria Lopez")
                .receiverPhone("3009876543")
                .price(BigDecimal.valueOf(25000))
                .statusParcel(StatusParcel.CREATED)
                .deliveryOtp("654321")
                .fromStop(fromStop)
                .toStop(toStop)
                .trip(null)
                .build();

        when(parcelMapper.toEntity(requestWithoutTrip)).thenReturn(parcelWithoutTrip);
        when(parcelRepository.save(any(Parcel.class))).thenReturn(parcelWithoutTrip);
        when(parcelMapper.toResponse(parcelWithoutTrip)).thenReturn(response);

        // Act
        ParcelDTO.parcelResponse result = parcelService.save(requestWithoutTrip);

        // Assert
        assertNotNull(result);
        verify(parcelRepository).save(parcelWithoutTrip);
    }

    @Test
    @DisplayName("GetAll - Debe retornar página de paquetes")
    void getAll_ShouldReturnPageOfParcels() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Parcel> parcels = List.of(parcel);
        Page<Parcel> parcelPage = new PageImpl<>(parcels, pageable, 1);

        when(parcelRepository.findAll(pageable)).thenReturn(parcelPage);
        when(parcelMapper.toResponse(parcel)).thenReturn(response);

        // Act
        Page<ParcelDTO.parcelResponse> result = parcelService.getAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(response.id(), result.getContent().get(0).id());

        verify(parcelRepository).findAll(pageable);
        verify(parcelMapper).toResponse(parcel);
    }

    @Test
    @DisplayName("GetAll - Debe retornar página vacía cuando no hay paquetes")
    void getAll_ShouldReturnEmptyPage_WhenNoParcels() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Parcel> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(parcelRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act
        Page<ParcelDTO.parcelResponse> result = parcelService.getAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(parcelRepository).findAll(pageable);
        verify(parcelMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Get by ID - Debe obtener paquete por ID")
    void get_ShouldReturnParcel_WhenIdExists() {
        // Arrange
        when(parcelRepository.findById(1L)).thenReturn(Optional.of(parcel));
        when(parcelMapper.toResponse(parcel)).thenReturn(response);

        // Act
        ParcelDTO.parcelResponse result = parcelService.get(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Juan Perez", result.senderName());
        assertEquals("Maria Lopez", result.receiverName());

        verify(parcelRepository).findById(1L);
        verify(parcelMapper).toResponse(parcel);
    }

    @Test
    @DisplayName("Get by ID - Debe lanzar excepción cuando el ID no existe")
    void get_ShouldThrowException_WhenIdNotFound() {
        // Arrange
        when(parcelRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> parcelService.get(999L)
        );

        assertEquals("Parcel not found", exception.getMessage());
        verify(parcelRepository).findById(999L);
        verify(parcelMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Get by TagCode - Debe obtener paquete por código")
    void testGet_ShouldReturnParcel_WhenCodeExists() {
        // Arrange
        String code = "PCL-1234567890";
        when(parcelRepository.findByCode(code)).thenReturn(Optional.of(parcel));
        when(parcelMapper.toResponse(parcel)).thenReturn(response);

        // Act
        ParcelDTO.parcelResponse result = parcelService.get(code);

        // Assert
        assertNotNull(result);
        assertEquals(code, result.code());
        assertEquals("Juan Perez", result.senderName());

        verify(parcelRepository).findByCode(code);
        verify(parcelMapper).toResponse(parcel);
    }

    @Test
    @DisplayName("Get by TagCode - Debe lanzar excepción cuando el código no existe")
    void testGet_ShouldThrowException_WhenCodeNotFound() {
        // Arrange
        String invalidCode = "PCL-INVALID";
        when(parcelRepository.findByCode(invalidCode)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> parcelService.get(invalidCode)
        );

        assertEquals("Parcel not found", exception.getMessage());
        verify(parcelRepository).findByCode(invalidCode);
        verify(parcelMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Delete - Debe eliminar paquete cuando existe")
    void delete_ShouldDeleteParcel_WhenExists() {
        // Arrange
        when(parcelRepository.findById(1L)).thenReturn(Optional.of(parcel));
        doNothing().when(parcelRepository).deleteById(1L);

        // Act
        boolean result = parcelService.delete(1L);

        // Assert
        assertTrue(result);
        verify(parcelRepository).findById(1L);
        verify(parcelRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Delete - Debe lanzar excepción cuando el paquete no existe")
    void delete_ShouldThrowException_WhenParcelNotFound() {
        // Arrange
        when(parcelRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> parcelService.delete(999L));

        verify(parcelRepository).findById(999L);
        verify(parcelRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("GetObject by ID - Debe retornar entidad Parcel cuando existe")
    void getObject_ShouldReturnParcel_WhenExists() {
        // Arrange
        when(parcelRepository.findById(1L)).thenReturn(Optional.of(parcel));

        // Act
        Parcel result = parcelService.getObject(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("PCL-1234567890", result.getCode());
        assertEquals("Juan Perez", result.getSenderName());
        assertEquals("Maria Lopez", result.getReceiverName());
        assertEquals(StatusParcel.CREATED, result.getStatusParcel());

        verify(parcelRepository).findById(1L);
    }

    @Test
    @DisplayName("GetObject by ID - Debe lanzar excepción cuando no existe")
    void getObject_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(parcelRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> parcelService.getObject(999L)
        );

        assertEquals("Parcel not found", exception.getMessage());
        verify(parcelRepository).findById(999L);
    }

    @Test
    @DisplayName("GetObject by Code - Debe retornar entidad Parcel cuando existe")
    void testGetObject_ShouldReturnParcel_WhenCodeExists() {
        // Arrange
        String code = "PCL-1234567890";
        when(parcelRepository.findByCode(code)).thenReturn(Optional.of(parcel));

        // Act
        Parcel result = parcelService.getObject(code);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(code, result.getCode());
        assertEquals("Juan Perez", result.getSenderName());

        verify(parcelRepository).findByCode(code);
    }

    @Test
    @DisplayName("GetObject by Code - Debe lanzar excepción cuando no existe")
    void testGetObject_ShouldThrowException_WhenCodeNotExists() {
        // Arrange
        String invalidCode = "PCL-INVALID";
        when(parcelRepository.findByCode(invalidCode)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> parcelService.getObject(invalidCode)
        );

        assertEquals("Parcel not found", exception.getMessage());
        verify(parcelRepository).findByCode(invalidCode);
    }

    @Test
    @DisplayName("Update - Debe actualizar paquete exitosamente")
    void update_ShouldUpdateParcelSuccessfully() {
        // Arrange
        when(parcelRepository.findById(1L)).thenReturn(Optional.of(parcel));
        doNothing().when(parcelMapper).updateEntity(updateRequest, parcel);
        when(parcelMapper.toResponse(parcel)).thenReturn(response);

        // Act
        ParcelDTO.parcelResponse result = parcelService.update(updateRequest, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(response.id(), result.id());

        verify(parcelRepository).findById(1L);
        verify(parcelMapper).updateEntity(updateRequest, parcel);
        verify(parcelMapper).toResponse(parcel);
    }

    @Test
    @DisplayName("Update - Debe cambiar status a IN_TRANSIT")
    void update_ShouldChangeStatusToInTransit() {
        // Arrange
        ParcelDTO.parcelUpdateRequest inTransitUpdate =
                new ParcelDTO.parcelUpdateRequest(
                        StatusParcel.IN_TRANSIT,
                        null,
                        null
                );

        when(parcelRepository.findById(1L)).thenReturn(Optional.of(parcel));
        doAnswer(invocation -> {
            Parcel p = invocation.getArgument(1);
            p.setStatusParcel(StatusParcel.IN_TRANSIT);
            return null;
        }).when(parcelMapper).updateEntity(inTransitUpdate, parcel);

        ParcelDTO.parcelResponse updatedResponse = new ParcelDTO.parcelResponse(
                1L, "PCL-1234567890", "Juan Perez", "3001234567",
                "Maria Lopez", "3009876543", BigDecimal.valueOf(25000),
                "IN_TRANSIT", null, "123456", LocalDateTime.now(), null, 1L, 2L, 1L
        );
        when(parcelMapper.toResponse(parcel)).thenReturn(updatedResponse);

        // Act
        ParcelDTO.parcelResponse result = parcelService.update(inTransitUpdate, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("IN_TRANSIT", result.statusParcel());
    }

    @Test
    @DisplayName("Update - Debe cambiar status a DELIVERED con proof photo")
    void update_ShouldChangeStatusToDeliveredWithProof() {
        // Arrange
        ParcelDTO.parcelUpdateRequest deliveredUpdate =
                new ParcelDTO.parcelUpdateRequest(
                        StatusParcel.DELIVERED,
                        "https://example.com/proof.jpg",
                        "123456"
                );

        when(parcelRepository.findById(1L)).thenReturn(Optional.of(parcel));
        doAnswer(invocation -> {
            Parcel p = invocation.getArgument(1);
            p.setStatusParcel(StatusParcel.DELIVERED);
            p.setProofPhotoUrl("https://example.com/proof.jpg");
            p.setDeliveredAt(LocalDateTime.now());
            return null;
        }).when(parcelMapper).updateEntity(deliveredUpdate, parcel);

        ParcelDTO.parcelResponse deliveredResponse = new ParcelDTO.parcelResponse(
                1L, "PCL-1234567890", "Juan Perez", "3001234567",
                "Maria Lopez", "3009876543", BigDecimal.valueOf(25000),
                "DELIVERED", "https://example.com/proof.jpg", "123456",
                LocalDateTime.now(), LocalDateTime.now(), 1L, 2L, 1L
        );
        when(parcelMapper.toResponse(parcel)).thenReturn(deliveredResponse);

        // Act
        ParcelDTO.parcelResponse result = parcelService.update(deliveredUpdate, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("DELIVERED", result.statusParcel());
        assertEquals("https://example.com/proof.jpg", result.proofPhotoUrl());
    }

    @Test
    @DisplayName("Update - Debe cambiar status a FAILED")
    void update_ShouldChangeStatusToFailed() {
        // Arrange
        ParcelDTO.parcelUpdateRequest failedUpdate =
                new ParcelDTO.parcelUpdateRequest(
                        StatusParcel.FAILED,
                        null,
                        null
                );

        when(parcelRepository.findById(1L)).thenReturn(Optional.of(parcel));
        doAnswer(invocation -> {
            Parcel p = invocation.getArgument(1);
            p.setStatusParcel(StatusParcel.FAILED);
            return null;
        }).when(parcelMapper).updateEntity(failedUpdate, parcel);

        ParcelDTO.parcelResponse failedResponse = new ParcelDTO.parcelResponse(
                1L, "PCL-1234567890", "Juan Perez", "3001234567",
                "Maria Lopez", "3009876543", BigDecimal.valueOf(25000),
                "FAILED", null, "123456", LocalDateTime.now(), null, 1L, 2L, 1L
        );
        when(parcelMapper.toResponse(parcel)).thenReturn(failedResponse);

        // Act
        ParcelDTO.parcelResponse result = parcelService.update(failedUpdate, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("FAILED", result.statusParcel());
    }

    @Test
    @DisplayName("Update - Debe lanzar excepción cuando el paquete no existe")
    void update_ShouldThrowException_WhenParcelNotFound() {
        // Arrange
        when(parcelRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> parcelService.update(updateRequest, 999L)
        );

        assertEquals("Parcel not found", exception.getMessage());
        verify(parcelRepository).findById(999L);
        verify(parcelMapper, never()).updateEntity(any(), any());
    }

    @Test
    @DisplayName("Save - Debe validar formato de teléfono de 10 dígitos")
    void save_ShouldValidatePhoneNumberFormat() {
        // Arrange
        when(parcelMapper.toEntity(createRequest)).thenReturn(parcel);
        when(parcelRepository.save(any(Parcel.class))).thenReturn(parcel);
        when(parcelMapper.toResponse(parcel)).thenReturn(response);

        // Act
        ParcelDTO.parcelResponse result = parcelService.save(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.senderPhone().length());
        assertEquals(10, result.receiverPhone().length());
        assertTrue(result.senderPhone().matches("\\d{10}"));
        assertTrue(result.receiverPhone().matches("\\d{10}"));
    }

    @Test
    @DisplayName("Update - Debe actualizar solo status sin proof photo")
    void update_ShouldUpdateOnlyStatus_WithoutProofPhoto() {
        // Arrange
        ParcelDTO.parcelUpdateRequest statusOnlyUpdate =
                new ParcelDTO.parcelUpdateRequest(
                        StatusParcel.IN_TRANSIT,
                        null,
                        null
                );

        when(parcelRepository.findById(1L)).thenReturn(Optional.of(parcel));
        doNothing().when(parcelMapper).updateEntity(statusOnlyUpdate, parcel);
        when(parcelMapper.toResponse(parcel)).thenReturn(response);

        // Act
        ParcelDTO.parcelResponse result = parcelService.update(statusOnlyUpdate, 1L);

        // Assert
        assertNotNull(result);
        verify(parcelMapper).updateEntity(statusOnlyUpdate, parcel);
    }
}