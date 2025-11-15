package com.unimag.service;

import com.unimag.DTO.FareRuleDTO;
import com.unimag.entities.FareRule;
import com.unimag.entities.Route;
import com.unimag.entities.Stop;
import com.unimag.entities.Enums.DynamicPricing;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.FareRuleMapper;
import com.unimag.repository.FareRuleRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FareRuleServiceImplTest {

    @Mock
    private FareRuleRepository fareRuleRepository;

    @Mock
    private FareRuleMapper fareRuleMapper;

    @Mock
    private StopService stopService;

    @InjectMocks
    private FareRuleServiceImpl fareRuleService;

    private FareRule fareRule;
    private Route route;
    private Stop originStop;
    private Stop destinationStop;
    private FareRuleDTO.fareRuleCreateRequest createRequest;
    private FareRuleDTO.fareRuleUpdateRequest updateRequest;
    private FareRuleDTO.fareRuleResponse response;
    private Map<String, Object> discounts;

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

        // Setup Origin Stop
        originStop = Stop.builder()
                .id(1L)
                .name("Terminal Santa Marta")
                .order(1)
                .lat(BigDecimal.valueOf(11.2408))
                .lng(BigDecimal.valueOf(-74.2120))
                .route(route)
                .build();

        // Setup Destination Stop
        destinationStop = Stop.builder()
                .id(2L)
                .name("Terminal Barranquilla")
                .order(5)
                .lat(BigDecimal.valueOf(10.9685))
                .lng(BigDecimal.valueOf(-74.7813))
                .route(route)
                .build();

        // Setup Discounts
        discounts = new HashMap<>();
        discounts.put("student", 0.15);
        discounts.put("senior", 0.20);

        // Setup FareRule
        fareRule = FareRule.builder()
                .id(1L)
                .basePrice(BigDecimal.valueOf(50000))
                .discounts(discounts)
                .dynamicPricing(DynamicPricing.OFF)
                .route(route)
                .fromStop(originStop)
                .toStop(destinationStop)
                .build();

        // Setup DTOs
        createRequest = new FareRuleDTO.fareRuleCreateRequest(
                BigDecimal.valueOf(50000), // basePrice
                discounts,                  // discounts
                DynamicPricing.OFF,        // dynamicPricing
                1L,                         // routeId
                1L,                         // fromStopId
                2L,                         // toStopId
                1L,                         // originId
                2L                          // destinationId
        );

        updateRequest = new FareRuleDTO.fareRuleUpdateRequest(
                BigDecimal.valueOf(55000), // basePrice
                discounts,                  // discounts
                DynamicPricing.ON,         // dynamicPricing
                1L,                         // originId
                2L                          // destinationId
        );

        response = new FareRuleDTO.fareRuleResponse(
                1L,
                BigDecimal.valueOf(50000),
                discounts,
                "OFF",
                1L,
                1L,
                2L,
                "Terminal Santa Marta",
                "Terminal Barranquilla"
        );
    }

    @Test
    @DisplayName("Save - Debe guardar una regla de tarifa exitosamente")
    void save_ShouldSaveFareRuleSuccessfully() {
        // Arrange
        when(fareRuleMapper.toEntity(createRequest)).thenReturn(fareRule);
        when(stopService.getObject(1L)).thenReturn(originStop);
        when(stopService.getObject(2L)).thenReturn(destinationStop);
        when(fareRuleRepository.save(any(FareRule.class))).thenReturn(fareRule);
        when(fareRuleMapper.toResponse(fareRule)).thenReturn(response);

        // Act
        FareRuleDTO.fareRuleResponse result = fareRuleService.save(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(response.id(), result.id());
        assertEquals(response.basePrice(), result.basePrice());
        assertEquals(response.fromStopName(), result.fromStopName());
        assertEquals(response.toStopName(), result.toStopName());

        verify(fareRuleMapper).toEntity(createRequest);
        verify(stopService).getObject(1L);
        verify(stopService).getObject(2L);
        verify(fareRuleRepository).save(fareRule);
        verify(fareRuleMapper).toResponse(fareRule);
    }

    @Test
    @DisplayName("Save - Debe lanzar excepción cuando origen y destino son iguales")
    void save_ShouldThrowException_WhenOriginAndDestinationAreSame() {
        // Arrange
        FareRuleDTO.fareRuleCreateRequest sameStopsRequest =
                new FareRuleDTO.fareRuleCreateRequest(
                        BigDecimal.valueOf(50000),
                        discounts,
                        DynamicPricing.OFF,
                        1L,
                        1L,
                        1L,
                        1L, // Same as destinationId
                        1L  // Same as originId
                );

        when(fareRuleMapper.toEntity(sameStopsRequest)).thenReturn(fareRule);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fareRuleService.save(sameStopsRequest)
        );

        assertEquals("origen debe ser diferente a destino", exception.getMessage());
        verify(fareRuleMapper).toEntity(sameStopsRequest);
        verify(stopService, never()).getObject(anyLong());
        verify(fareRuleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Save - Debe lanzar excepción cuando el stop de origen no existe")
    void save_ShouldThrowException_WhenOriginStopNotFound() {
        // Arrange
        when(fareRuleMapper.toEntity(createRequest)).thenReturn(fareRule);
        when(stopService.getObject(1L))
                .thenThrow(new NotFoundException("Stop not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> fareRuleService.save(createRequest));

        verify(fareRuleMapper).toEntity(createRequest);
        verify(stopService).getObject(1L);
        verify(fareRuleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Save - Debe lanzar excepción cuando el stop de destino no existe")
    void save_ShouldThrowException_WhenDestinationStopNotFound() {
        // Arrange
        when(fareRuleMapper.toEntity(createRequest)).thenReturn(fareRule);
        when(stopService.getObject(1L)).thenReturn(originStop);
        when(stopService.getObject(2L))
                .thenThrow(new NotFoundException("Stop not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> fareRuleService.save(createRequest));

        verify(fareRuleMapper).toEntity(createRequest);
        verify(stopService).getObject(1L);
        verify(stopService).getObject(2L);
        verify(fareRuleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get - Debe obtener una regla de tarifa por ID")
    void get_ShouldReturnFareRule_WhenIdExists() {
        // Arrange
        when(fareRuleRepository.findById(1L)).thenReturn(Optional.of(fareRule));
        when(fareRuleMapper.toResponse(fareRule)).thenReturn(response);

        // Act
        FareRuleDTO.fareRuleResponse result = fareRuleService.get(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(BigDecimal.valueOf(50000), result.basePrice());
        assertEquals("Terminal Santa Marta", result.fromStopName());

        verify(fareRuleRepository).findById(1L);
        verify(fareRuleMapper).toResponse(fareRule);
    }

    @Test
    @DisplayName("Get - Debe lanzar excepción cuando el ID no existe")
    void get_ShouldThrowException_WhenIdNotFound() {
        // Arrange
        when(fareRuleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> fareRuleService.get(999L)
        );

        assertEquals("fareRule not found", exception.getMessage());
        verify(fareRuleRepository).findById(999L);
        verify(fareRuleMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("GetAll - Debe retornar página de reglas de tarifa")
    void getAll_ShouldReturnPageOfFareRules() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<FareRule> fareRules = List.of(fareRule);
        Page<FareRule> fareRulePage = new PageImpl<>(fareRules, pageable, 1);

        when(fareRuleRepository.findAll(pageable)).thenReturn(fareRulePage);
        when(fareRuleMapper.toResponse(fareRule)).thenReturn(response);

        // Act
        Page<FareRuleDTO.fareRuleResponse> result = fareRuleService.getAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(response.id(), result.getContent().get(0).id());

        verify(fareRuleRepository).findAll(pageable);
        verify(fareRuleMapper).toResponse(fareRule);
    }

    @Test
    @DisplayName("GetAll - Debe retornar página vacía cuando no hay reglas")
    void getAll_ShouldReturnEmptyPage_WhenNoFareRules() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<FareRule> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(fareRuleRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act
        Page<FareRuleDTO.fareRuleResponse> result = fareRuleService.getAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(fareRuleRepository).findAll(pageable);
        verify(fareRuleMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("GetObject - Debe retornar entidad FareRule cuando existe")
    void getObject_ShouldReturnFareRule_WhenExists() {
        // Arrange
        when(fareRuleRepository.findById(1L)).thenReturn(Optional.of(fareRule));

        // Act
        FareRule result = fareRuleService.getObject(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BigDecimal.valueOf(50000), result.getBasePrice());
        assertEquals(DynamicPricing.OFF, result.getDynamicPricing());
        assertEquals(originStop.getId(), result.getFromStop().getId());
        assertEquals(destinationStop.getId(), result.getToStop().getId());

        verify(fareRuleRepository).findById(1L);
    }

    @Test
    @DisplayName("GetObject - Debe lanzar excepción cuando no existe")
    void getObject_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(fareRuleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> fareRuleService.getObject(999L)
        );

        assertEquals("fareRule not found", exception.getMessage());
        verify(fareRuleRepository).findById(999L);
    }

    @Test
    @DisplayName("Delete - Debe eliminar regla de tarifa")
    void delete_ShouldDeleteFareRule() {
        // Arrange
        doNothing().when(fareRuleRepository).deleteById(1L);

        // Act
        boolean result = fareRuleService.delete(1L);

        // Assert
        assertTrue(result);
        verify(fareRuleRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Update - Debe actualizar regla de tarifa exitosamente")
    void update_ShouldUpdateFareRuleSuccessfully() {
        // Arrange
        when(fareRuleRepository.findById(1L)).thenReturn(Optional.of(fareRule));
        when(stopService.getObject(1L)).thenReturn(originStop);
        when(stopService.getObject(2L)).thenReturn(destinationStop);
        doNothing().when(fareRuleMapper).updateEntity(updateRequest, fareRule);
        when(fareRuleMapper.toResponse(fareRule)).thenReturn(response);

        // Act
        FareRuleDTO.fareRuleResponse result = fareRuleService.update(updateRequest, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(response.id(), result.id());

        verify(fareRuleRepository).findById(1L);
        verify(fareRuleMapper).updateEntity(updateRequest, fareRule);
        verify(stopService).getObject(1L);
        verify(stopService).getObject(2L);
        verify(fareRuleMapper).toResponse(fareRule);
    }

    @Test
    @DisplayName("Update - Debe actualizar solo precio base cuando no hay otros cambios")
    void update_ShouldUpdateOnlyBasePrice_WhenNoOtherChanges() {

        FareRuleDTO.fareRuleUpdateRequest partialUpdate =
                new FareRuleDTO.fareRuleUpdateRequest(
                        BigDecimal.valueOf(60000),
                        null,
                        null,
                        null,
                        null
                );

        when(fareRuleRepository.findById(1L)).thenReturn(Optional.of(fareRule));
        doNothing().when(fareRuleMapper).updateEntity(partialUpdate, fareRule);
        when(fareRuleMapper.toResponse(fareRule)).thenReturn(response);

        FareRuleDTO.fareRuleResponse result = fareRuleService.update(partialUpdate, 1L);
        assertNotNull(result);

        verify(fareRuleRepository).findById(1L);
        verify(fareRuleMapper).updateEntity(partialUpdate, fareRule);
        verify(stopService, never()).getObject(anyLong());
        verify(fareRuleMapper).toResponse(fareRule);
    }

    @Test
    @DisplayName("Update - Debe lanzar excepción cuando origen y destino son iguales")
    void update_ShouldThrowException_WhenOriginAndDestinationAreSame() {

        FareRuleDTO.fareRuleUpdateRequest sameStopsUpdate =
                new FareRuleDTO.fareRuleUpdateRequest(
                        BigDecimal.valueOf(50000),
                        discounts,
                        DynamicPricing.ON,
                        1L,
                        1L
                );

        when(fareRuleRepository.findById(1L)).thenReturn(Optional.of(fareRule));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fareRuleService.update(sameStopsUpdate, 1L)
        );

        assertEquals("originId and destinationId cannot be the same", exception.getMessage());
        verify(fareRuleRepository).findById(1L);
        verify(fareRuleMapper, never()).updateEntity(any(), any());
    }

    @Test
    @DisplayName("Update - Debe actualizar solo el origin stop cuando solo originId cambia")
    void update_ShouldUpdateOnlyOriginStop_WhenOnlyOriginIdChanges() {
        // Arrange
        FareRuleDTO.fareRuleUpdateRequest originOnlyUpdate =
                new FareRuleDTO.fareRuleUpdateRequest(
                        null,
                        null,
                        null,
                        3L,  // New originId
                        null
                );

        Stop newOriginStop = Stop.builder()
                .id(3L)
                .name("Terminal Ciénaga")
                .build();

        when(fareRuleRepository.findById(1L)).thenReturn(Optional.of(fareRule));
        when(stopService.getObject(3L)).thenReturn(newOriginStop);
        doNothing().when(fareRuleMapper).updateEntity(originOnlyUpdate, fareRule);
        when(fareRuleMapper.toResponse(fareRule)).thenReturn(response);

        // Act
        FareRuleDTO.fareRuleResponse result = fareRuleService.update(originOnlyUpdate, 1L);

        // Assert
        assertNotNull(result);

        verify(fareRuleRepository).findById(1L);
        verify(fareRuleMapper).updateEntity(originOnlyUpdate, fareRule);
        verify(stopService).getObject(3L);
        verify(stopService, never()).getObject(2L);
    }

    @Test
    @DisplayName("Update - Debe actualizar solo el destination stop cuando solo destinationId cambia")
    void update_ShouldUpdateOnlyDestinationStop_WhenOnlyDestinationIdChanges() {
        // Arrange
        FareRuleDTO.fareRuleUpdateRequest destinationOnlyUpdate =
                new FareRuleDTO.fareRuleUpdateRequest(
                        null,
                        null,
                        null,
                        null,
                        4L  // New destinationId
                );

        Stop newDestinationStop = Stop.builder()
                .id(4L)
                .name("Terminal Cartagena")
                .build();

        when(fareRuleRepository.findById(1L)).thenReturn(Optional.of(fareRule));
        when(stopService.getObject(4L)).thenReturn(newDestinationStop);
        doNothing().when(fareRuleMapper).updateEntity(destinationOnlyUpdate, fareRule);
        when(fareRuleMapper.toResponse(fareRule)).thenReturn(response);

        // Act
        FareRuleDTO.fareRuleResponse result = fareRuleService.update(destinationOnlyUpdate, 1L);

        // Assert
        assertNotNull(result);

        verify(fareRuleRepository).findById(1L);
        verify(fareRuleMapper).updateEntity(destinationOnlyUpdate, fareRule);
        verify(stopService).getObject(4L);
        verify(stopService, never()).getObject(1L);
    }

    @Test
    @DisplayName("Update - Debe lanzar excepción cuando la regla no existe")
    void update_ShouldThrowException_WhenFareRuleNotFound() {
        // Arrange
        when(fareRuleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> fareRuleService.update(updateRequest, 999L)
        );

        assertEquals("fareRule not found", exception.getMessage());
        verify(fareRuleRepository).findById(999L);
        verify(fareRuleMapper, never()).updateEntity(any(), any());
    }

    @Test
    @DisplayName("Save - Debe guardar regla con DynamicPricing ON")
    void save_ShouldSaveFareRuleWithDynamicPricingOn() {
        // Arrange
        FareRuleDTO.fareRuleCreateRequest dynamicRequest =
                new FareRuleDTO.fareRuleCreateRequest(
                        BigDecimal.valueOf(50000),
                        discounts,
                        DynamicPricing.ON, // Dynamic pricing enabled
                        1L, 1L, 2L, 1L, 2L
                );

        FareRule dynamicFareRule = FareRule.builder()
                .id(2L)
                .basePrice(BigDecimal.valueOf(50000))
                .discounts(discounts)
                .dynamicPricing(DynamicPricing.ON)
                .route(route)
                .fromStop(originStop)
                .toStop(destinationStop)
                .build();

        when(fareRuleMapper.toEntity(dynamicRequest)).thenReturn(dynamicFareRule);
        when(stopService.getObject(1L)).thenReturn(originStop);
        when(stopService.getObject(2L)).thenReturn(destinationStop);
        when(fareRuleRepository.save(any(FareRule.class))).thenReturn(dynamicFareRule);

        FareRuleDTO.fareRuleResponse dynamicResponse =
                new FareRuleDTO.fareRuleResponse(2L, BigDecimal.valueOf(50000),
                        discounts, "ON", 1L, 1L, 2L,
                        "Terminal Santa Marta", "Terminal Barranquilla");
        when(fareRuleMapper.toResponse(dynamicFareRule)).thenReturn(dynamicResponse);

        // Act
        FareRuleDTO.fareRuleResponse result = fareRuleService.save(dynamicRequest);

        // Assert
        assertNotNull(result);
        assertEquals("ON", result.dynamicPricing());
    }

    @Test
    @DisplayName("Save - Debe guardar regla sin descuentos")
    void save_ShouldSaveFareRuleWithoutDiscounts() {
        // Arrange
        FareRuleDTO.fareRuleCreateRequest noDiscountsRequest =
                new FareRuleDTO.fareRuleCreateRequest(
                        BigDecimal.valueOf(50000),
                        new HashMap<>(), // Empty discounts
                        DynamicPricing.OFF,
                        1L, 1L, 2L, 1L, 2L
                );

        FareRule noDiscountsFareRule = FareRule.builder()
                .id(3L)
                .basePrice(BigDecimal.valueOf(50000))
                .discounts(new HashMap<>())
                .dynamicPricing(DynamicPricing.OFF)
                .route(route)
                .fromStop(originStop)
                .toStop(destinationStop)
                .build();

        when(fareRuleMapper.toEntity(noDiscountsRequest)).thenReturn(noDiscountsFareRule);
        when(stopService.getObject(1L)).thenReturn(originStop);
        when(stopService.getObject(2L)).thenReturn(destinationStop);
        when(fareRuleRepository.save(any(FareRule.class))).thenReturn(noDiscountsFareRule);
        when(fareRuleMapper.toResponse(noDiscountsFareRule)).thenReturn(response);

        // Act
        FareRuleDTO.fareRuleResponse result = fareRuleService.save(noDiscountsRequest);

        // Assert
        assertNotNull(result);
        verify(fareRuleRepository).save(noDiscountsFareRule);
    }
}