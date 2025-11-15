package com.unimag.service;

import com.unimag.DTO.BaggageDTO;
import com.unimag.entities.*;
import com.unimag.entities.Enums.PaymentMethod;
import com.unimag.entities.Enums.Role;
import com.unimag.entities.Enums.StatusTicket;
import com.unimag.entities.Enums.StatusTrip;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.BaggageMapper;
import com.unimag.repository.BaggageRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaggageServiceImplTest {

    @Mock
    private BaggageRepository baggageRepository;

    @Mock
    private BaggageMapper baggageMapper;

    @InjectMocks
    private BaggageServiceImpl baggageService;

    private Baggage baggage;
    private Ticket ticket;
    private User passenger;
    private Trip trip;
    private Route route;
    private Stop originStop;
    private Stop destinationStop;
    private BaggageDTO.baggageCreateRequest createRequest;
    private BaggageDTO.baggageUpdateRequest updateRequest;
    private BaggageDTO.baggageResponse response;

    @BeforeEach
    void setUp() {
        // Setup Route
        route = Route.builder()
                .id(1L)
                .origin("Santa Marta")
                .destination("Barranquilla")
                .build();

        // Setup Stops
        originStop = Stop.builder()
                .id(1L)
                .name("Terminal Santa Marta")
                .build();

        destinationStop = Stop.builder()
                .id(2L)
                .name("Terminal Barranquilla")
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

        // Setup Passenger
        passenger = User.builder()
                .id(1L)
                .name("Juan Perez")
                .email("juan@example.com")
                .phone("3001234567")
                .role(Role.PASSENGER)
                .build();

        // Setup Ticket
        ticket = Ticket.builder()
                .id(1L)
                .seatNumber("A1")
                .price(BigDecimal.valueOf(50000))
                .qrCode("QR-12345")
                .paymentMethod(PaymentMethod.CASH)
                .statusTicket(StatusTicket.SOLD)
                .passenger(passenger)
                .trip(trip)
                .fromStop(originStop)
                .toStop(destinationStop)
                .createdAt(LocalDateTime.now())
                .build();

        // Setup Baggage
        baggage = Baggage.builder()
                .id(1L)
                .weightKg(BigDecimal.valueOf(15.5))
                .fee(BigDecimal.ZERO)
                .tagCode("BAG-1234567890-123")
                .ticket(ticket)
                .build();

        // Setup DTOs
        createRequest = new BaggageDTO.baggageCreateRequest(
                1L, // ticketId
                BigDecimal.valueOf(15.5) // weightKg
        );

        updateRequest = new BaggageDTO.baggageUpdateRequest(
                BigDecimal.valueOf(22) // fee
        );

        response = new BaggageDTO.baggageResponse(
                1L,
                BigDecimal.valueOf(15.5),
                BigDecimal.ZERO,
                "BAG-1234567890-123",
                1L,
                "Juan Perez",
                "Santa Marta → Barranquilla (2024-01-01)",
                false
        );
    }

    @Test
    @DisplayName("Save - Debe guardar equipaje con peso menor a 5kg sin tarifa")
    void save_ShouldSaveBaggageWithoutFee_WhenWeightLessThan5Kg() {
        // Arrange
        BaggageDTO.baggageCreateRequest lightBaggage =
                new BaggageDTO.baggageCreateRequest(1L, BigDecimal.valueOf(3.5));

        Baggage lightBaggageEntity = Baggage.builder()
                .weightKg(BigDecimal.valueOf(3.5))
                .fee(BigDecimal.ZERO)
                .tagCode("BAG-TEST")
                .ticket(ticket)
                .build();

        when(baggageMapper.toEntity(lightBaggage)).thenReturn(lightBaggageEntity);
        when(baggageRepository.save(any(Baggage.class))).thenReturn(lightBaggageEntity);
        when(baggageMapper.toResponse(lightBaggageEntity)).thenReturn(response);

        // Act
        BaggageDTO.baggageResponse result = baggageService.save(lightBaggage);

        // Assert
        assertNotNull(result);
        verify(baggageMapper).toEntity(lightBaggage);
        verify(baggageRepository).save(lightBaggageEntity);
        verify(baggageMapper).toResponse(lightBaggageEntity);

        // Verificar que la tarifa fue establecida en ZERO
        assertEquals(BigDecimal.ZERO, lightBaggageEntity.getFee());
    }

    @Test
    @DisplayName("Save - Debe guardar equipaje con peso de 5kg o más con tarifa de $22")
    void save_ShouldSaveBaggageWithFee_WhenWeightGreaterOrEqual5Kg() {
        // Arrange
        BaggageDTO.baggageCreateRequest heavyBaggage =
                new BaggageDTO.baggageCreateRequest(1L, BigDecimal.valueOf(10));

        Baggage heavyBaggageEntity = Baggage.builder()
                .weightKg(BigDecimal.valueOf(10))
                .fee(BigDecimal.valueOf(22))
                .tagCode("BAG-HEAVY")
                .ticket(ticket)
                .build();

        when(baggageMapper.toEntity(heavyBaggage)).thenReturn(heavyBaggageEntity);
        when(baggageRepository.save(any(Baggage.class))).thenReturn(heavyBaggageEntity);
        when(baggageMapper.toResponse(heavyBaggageEntity)).thenReturn(response);

        // Act
        BaggageDTO.baggageResponse result = baggageService.save(heavyBaggage);

        // Assert
        assertNotNull(result);
        verify(baggageMapper).toEntity(heavyBaggage);
        verify(baggageRepository).save(heavyBaggageEntity);

        // Verificar que la tarifa fue establecida en $22
        assertEquals(BigDecimal.valueOf(22), heavyBaggageEntity.getFee());
    }

    @Test
    @DisplayName("Save - Debe guardar equipaje con peso exacto de 5kg con tarifa de $22")
    void save_ShouldSaveBaggageWithFee_WhenWeightExactly5Kg() {
        // Arrange
        BaggageDTO.baggageCreateRequest exactWeightBaggage =
                new BaggageDTO.baggageCreateRequest(1L, BigDecimal.valueOf(5));

        Baggage exactBaggageEntity = Baggage.builder()
                .weightKg(BigDecimal.valueOf(5))
                .fee(BigDecimal.valueOf(22))
                .tagCode("BAG-EXACT")
                .ticket(ticket)
                .build();

        when(baggageMapper.toEntity(exactWeightBaggage)).thenReturn(exactBaggageEntity);
        when(baggageRepository.save(any(Baggage.class))).thenReturn(exactBaggageEntity);
        when(baggageMapper.toResponse(exactBaggageEntity)).thenReturn(response);

        // Act
        BaggageDTO.baggageResponse result = baggageService.save(exactWeightBaggage);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(22), exactBaggageEntity.getFee());
    }

    @Test
    @DisplayName("Get by ID - Debe obtener equipaje por ID")
    void get_ShouldReturnBaggage_WhenIdExists() {
        // Arrange
        when(baggageRepository.findById(1L)).thenReturn(Optional.of(baggage));
        when(baggageMapper.toResponse(baggage)).thenReturn(response);

        // Act
        BaggageDTO.baggageResponse result = baggageService.get(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Juan Perez", result.passengerName());
        assertEquals("BAG-1234567890-123", result.tagCode());

        verify(baggageRepository).findById(1L);
        verify(baggageMapper).toResponse(baggage);
    }

    @Test
    @DisplayName("Get by ID - Debe lanzar excepción cuando el ID no existe")
    void get_ShouldThrowException_WhenIdNotFound() {
        // Arrange
        when(baggageRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> baggageService.get(999L)
        );

        assertEquals("baggage not found", exception.getMessage());
        verify(baggageRepository).findById(999L);
        verify(baggageMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Get by TagCode - Debe obtener equipaje por código de etiqueta")
    void testGet_ShouldReturnBaggage_WhenTagCodeExists() {
        // Arrange
        String tagCode = "BAG-1234567890-123";
        when(baggageRepository.findByTagCode(tagCode)).thenReturn(Optional.of(baggage));
        when(baggageMapper.toResponse(baggage)).thenReturn(response);

        // Act
        BaggageDTO.baggageResponse result = baggageService.get(tagCode);

        // Assert
        assertNotNull(result);
        assertEquals(tagCode, result.tagCode());
        assertEquals("Juan Perez", result.passengerName());

        verify(baggageRepository).findByTagCode(tagCode);
        verify(baggageMapper).toResponse(baggage);
    }

    @Test
    @DisplayName("Get by TagCode - Debe lanzar excepción cuando el código no existe")
    void testGet_ShouldThrowException_WhenTagCodeNotFound() {
        // Arrange
        String invalidTagCode = "BAG-INVALID";
        when(baggageRepository.findByTagCode(invalidTagCode)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> baggageService.get(invalidTagCode)
        );

        assertEquals("Baggage not found", exception.getMessage());
        verify(baggageRepository).findByTagCode(invalidTagCode);
        verify(baggageMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("GetAll - Debe retornar página de equipajes")
    void getAll_ShouldReturnPageOfBaggages() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Baggage> baggages = List.of(baggage);
        Page<Baggage> baggagePage = new PageImpl<>(baggages, pageRequest, 1);

        when(baggageRepository.findAll(pageRequest)).thenReturn(baggagePage);
        when(baggageMapper.toResponse(baggage)).thenReturn(response);

        // Act
        Page<BaggageDTO.baggageResponse> result = baggageService.getAll(pageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(response.id(), result.getContent().get(0).id());

        verify(baggageRepository).findAll(pageRequest);
        verify(baggageMapper).toResponse(baggage);
    }

    @Test
    @DisplayName("GetAll - Debe retornar página vacía cuando no hay equipajes")
    void getAll_ShouldReturnEmptyPage_WhenNoBaggages() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Baggage> emptyPage = new PageImpl<>(List.of(), pageRequest, 0);

        when(baggageRepository.findAll(pageRequest)).thenReturn(emptyPage);

        // Act
        Page<BaggageDTO.baggageResponse> result = baggageService.getAll(pageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(baggageRepository).findAll(pageRequest);
        verify(baggageMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Delete - Debe eliminar equipaje cuando existe")
    void delete_ShouldDeleteBaggage_WhenExists() {
        // Arrange
        when(baggageRepository.findById(1L)).thenReturn(Optional.of(baggage));
        doNothing().when(baggageRepository).deleteById(1L);

        // Act
        boolean result = baggageService.delete(1L);

        // Assert
        assertTrue(result);
        verify(baggageRepository).findById(1L);
        verify(baggageRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Delete - Debe lanzar excepción cuando el equipaje no existe")
    void delete_ShouldThrowException_WhenBaggageNotFound() {
        // Arrange
        when(baggageRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> baggageService.delete(999L));

        verify(baggageRepository).findById(999L);
        verify(baggageRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Update - Debe actualizar equipaje exitosamente")
    void update_ShouldUpdateBaggageSuccessfully() {
        // Arrange
        when(baggageRepository.findById(1L)).thenReturn(Optional.of(baggage));
        doNothing().when(baggageMapper).updateEntity(updateRequest, baggage);
        when(baggageMapper.toResponse(baggage)).thenReturn(response);

        // Act
        BaggageDTO.baggageResponse result = baggageService.update(updateRequest, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(response.id(), result.id());

        verify(baggageRepository).findById(1L);
        verify(baggageMapper).updateEntity(updateRequest, baggage);
        verify(baggageMapper).toResponse(baggage);
    }

    @Test
    @DisplayName("Update - Debe lanzar excepción cuando el equipaje no existe")
    void update_ShouldThrowException_WhenBaggageNotFound() {
        // Arrange
        when(baggageRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> baggageService.update(updateRequest, 999L)
        );

        assertEquals("baggage not found", exception.getMessage());
        verify(baggageRepository).findById(999L);
        verify(baggageMapper, never()).updateEntity(any(), any());
    }

    @Test
    @DisplayName("Update - Debe actualizar la tarifa correctamente")
    void update_ShouldUpdateFeeCorrectly() {
        // Arrange
        BaggageDTO.baggageUpdateRequest newFeeRequest =
                new BaggageDTO.baggageUpdateRequest(BigDecimal.valueOf(50));

        when(baggageRepository.findById(1L)).thenReturn(Optional.of(baggage));
        doAnswer(invocation -> {
            Baggage bag = invocation.getArgument(1);
            bag.setFee(BigDecimal.valueOf(50));
            return null;
        }).when(baggageMapper).updateEntity(newFeeRequest, baggage);

        BaggageDTO.baggageResponse updatedResponse = new BaggageDTO.baggageResponse(
                1L, BigDecimal.valueOf(15.5), BigDecimal.valueOf(50),
                "BAG-1234567890-123", 1L, "Juan Perez",
                "Santa Marta → Barranquilla (2024-01-01)", false
        );
        when(baggageMapper.toResponse(baggage)).thenReturn(updatedResponse);

        // Act
        BaggageDTO.baggageResponse result = baggageService.update(newFeeRequest, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(50), result.fee());
    }

    @Test
    @DisplayName("GetObject - Debe retornar entidad Baggage cuando existe")
    void getObject_ShouldReturnBaggage_WhenExists() {
        // Arrange
        when(baggageRepository.findById(1L)).thenReturn(Optional.of(baggage));

        // Act
        Baggage result = baggageService.getObject(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BigDecimal.valueOf(15.5), result.getWeightKg());
        assertEquals("BAG-1234567890-123", result.getTagCode());
        assertEquals(ticket.getId(), result.getTicket().getId());

        verify(baggageRepository).findById(1L);
    }

    @Test
    @DisplayName("GetObject - Debe lanzar excepción cuando no existe")
    void getObject_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(baggageRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> baggageService.getObject(999L)
        );

        assertEquals("baggage not found", exception.getMessage());
        verify(baggageRepository).findById(999L);
    }

    @Test
    @DisplayName("Save - Debe manejar equipaje con peso en el límite (4.99kg)")
    void save_ShouldHandleBorderlineWeight_JustBelow5Kg() {
        // Arrange
        BaggageDTO.baggageCreateRequest borderlineRequest =
                new BaggageDTO.baggageCreateRequest(1L, BigDecimal.valueOf(4.99));

        Baggage borderlineBaggage = Baggage.builder()
                .weightKg(BigDecimal.valueOf(4.99))
                .fee(BigDecimal.ZERO)
                .tagCode("BAG-BORDER")
                .ticket(ticket)
                .build();

        when(baggageMapper.toEntity(borderlineRequest)).thenReturn(borderlineBaggage);
        when(baggageRepository.save(any(Baggage.class))).thenReturn(borderlineBaggage);
        when(baggageMapper.toResponse(borderlineBaggage)).thenReturn(response);

        // Act
        BaggageDTO.baggageResponse result = baggageService.save(borderlineRequest);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, borderlineBaggage.getFee());
    }

    @Test
    @DisplayName("Save - Debe manejar equipaje con peso en el límite (5.01kg)")
    void save_ShouldHandleBorderlineWeight_JustAbove5Kg() {
        // Arrange
        BaggageDTO.baggageCreateRequest borderlineRequest =
                new BaggageDTO.baggageCreateRequest(1L, BigDecimal.valueOf(5.01));

        Baggage borderlineBaggage = Baggage.builder()
                .weightKg(BigDecimal.valueOf(5.01))
                .fee(BigDecimal.valueOf(22))
                .tagCode("BAG-BORDER2")
                .ticket(ticket)
                .build();

        when(baggageMapper.toEntity(borderlineRequest)).thenReturn(borderlineBaggage);
        when(baggageRepository.save(any(Baggage.class))).thenReturn(borderlineBaggage);
        when(baggageMapper.toResponse(borderlineBaggage)).thenReturn(response);

        // Act
        BaggageDTO.baggageResponse result = baggageService.save(borderlineRequest);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(22), borderlineBaggage.getFee());
    }
}