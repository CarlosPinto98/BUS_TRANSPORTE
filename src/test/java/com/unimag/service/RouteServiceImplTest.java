package com.unimag.service;

import com.unimag.DTO.RouteDTO;
import com.unimag.entities.Route;
import com.unimag.entities.Stop;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.RouteMapper;
import com.unimag.repository.RouteRepository;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceImplTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private RouteMapper routeMapper;

    @Mock
    private StopService stopService;

    @InjectMocks
    private RouteServiceImpl routeService;

    private Route route;
    private Stop originStop;
    private Stop destinationStop;
    private Stop intermediateStop;
    private RouteDTO.routeCreateRequest createRequest;
    private RouteDTO.routeUpdateRequest updateRequest;
    private RouteDTO.routeResponse response;
    private List<RouteDTO.stopSummary> stopSummaries;

    @BeforeEach
    void setUp() {
        // Setup Origin Stop
        originStop = Stop.builder()
                .id(1L)
                .name("Terminal Santa Marta")
                .order(1)
                .lat(BigDecimal.valueOf(11.2408))
                .lng(BigDecimal.valueOf(-74.2120))
                .originRoutes(new HashSet<>())
                .destinationRoutes(new HashSet<>())
                .build();

        // Setup Intermediate Stop
        intermediateStop = Stop.builder()
                .id(2L)
                .name("Terminal Ciénaga")
                .order(2)
                .lat(BigDecimal.valueOf(11.0070))
                .lng(BigDecimal.valueOf(-74.2493))
                .build();

        // Setup Destination Stop
        destinationStop = Stop.builder()
                .id(3L)
                .name("Terminal Barranquilla")
                .order(3)
                .lat(BigDecimal.valueOf(10.9685))
                .lng(BigDecimal.valueOf(-74.7813))
                .originRoutes(new HashSet<>())
                .destinationRoutes(new HashSet<>())
                .build();

        // Setup Route
        route = Route.builder()
                .id(1L)
                .code("RT-001")
                .name("Ruta Santa Marta - Barranquilla")
                .origin("Santa Marta")
                .destination("Barranquilla")
                .distanceKm(100)
                .durationMin(120)
                .stops(new ArrayList<>(List.of(originStop, intermediateStop, destinationStop)))
                .originStop(originStop)
                .destinationStop(destinationStop)
                .trips(new ArrayList<>())
                .build();

        // Setup Stop Summaries
        stopSummaries = List.of(
                new RouteDTO.stopSummary(1L, "Terminal Santa Marta", 1,
                        BigDecimal.valueOf(11.2408), BigDecimal.valueOf(-74.2120)),
                new RouteDTO.stopSummary(2L, "Terminal Ciénaga", 2,
                        BigDecimal.valueOf(11.0070), BigDecimal.valueOf(-74.2493)),
                new RouteDTO.stopSummary(3L, "Terminal Barranquilla", 3,
                        BigDecimal.valueOf(10.9685), BigDecimal.valueOf(-74.7813))
        );

        // Setup DTOs
        createRequest = new RouteDTO.routeCreateRequest(
                "RT-001",           // code
                "Ruta Santa Marta - Barranquilla", // name
                "Santa Marta",      // origin
                "Barranquilla",     // destination
                100,                // distanceKm
                120,                // durationMin
                1L,                 // originId
                3L                  // destinationId
        );

        updateRequest = new RouteDTO.routeUpdateRequest(
                "Ruta Santa Marta - Barranquilla Express", // name
                110,                // distanceKm
                115,                // durationMin
                1L,                 // originId
                3L                  // destinationId
        );

        response = new RouteDTO.routeResponse(
                1L,
                "RT-001",
                "Ruta Santa Marta - Barranquilla",
                "Santa Marta",
                "Barranquilla",
                100,
                120,
                stopSummaries
        );
    }

    @Test
    @DisplayName("Delete - Debe eliminar una ruta")
    void delete_ShouldDeleteRoute() {
        // Arrange
        doNothing().when(routeRepository).deleteById(1L);

        // Act
        assertDoesNotThrow(() -> routeService.delete(1L));

        // Assert
        verify(routeRepository).deleteById(1L);
    }

    @Test
    @DisplayName("GetObject - Debe retornar entidad Route cuando existe")
    void getObject_ShouldReturnRoute_WhenExists() {
        // Arrange
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));

        // Act
        Route result = routeService.getObject(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("RT-001", result.getCode());
        assertEquals("Ruta Santa Marta - Barranquilla", result.getName());
        assertEquals("Santa Marta", result.getOrigin());
        assertEquals("Barranquilla", result.getDestination());
        assertEquals(100, result.getDistanceKm());
        assertEquals(120, result.getDurationMin());

        verify(routeRepository).findById(1L);
    }

    @Test
    @DisplayName("GetObject - Debe lanzar excepción cuando no existe")
    void getObject_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(routeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> routeService.getObject(999L)
        );

        assertEquals("Route not found", exception.getMessage());
        verify(routeRepository).findById(999L);
    }

    @Test
    @DisplayName("Save - Debe guardar una ruta exitosamente")
    void save_ShouldSaveRouteSuccessfully() {
        // Arrange
        when(routeMapper.toEntity(createRequest)).thenReturn(route);
        when(stopService.getObject(1L)).thenReturn(originStop);
        when(stopService.getObject(3L)).thenReturn(destinationStop);
        when(routeRepository.save(any(Route.class))).thenReturn(route);
        when(routeMapper.toResponse(route)).thenReturn(response);

        // Act
        RouteDTO.routeResponse result = routeService.save(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(response.id(), result.id());
        assertEquals(response.code(), result.code());
        assertEquals(response.name(), result.name());
        assertEquals(response.origin(), result.origin());
        assertEquals(response.destination(), result.destination());

        verify(routeMapper).toEntity(createRequest);
        verify(stopService).getObject(1L);
        verify(stopService).getObject(3L);
        verify(routeRepository).save(route);
        verify(routeMapper).toResponse(route);
    }

    @Test
    @DisplayName("Save - Debe establecer origin y destination stops correctamente")
    void save_ShouldSetOriginAndDestinationStopsCorrectly() {
        // Arrange
        when(routeMapper.toEntity(createRequest)).thenReturn(route);
        when(stopService.getObject(1L)).thenReturn(originStop);
        when(stopService.getObject(3L)).thenReturn(destinationStop);
        when(routeRepository.save(any(Route.class))).thenReturn(route);
        when(routeMapper.toResponse(route)).thenReturn(response);

        // Act
        RouteDTO.routeResponse result = routeService.save(createRequest);

        // Assert
        assertNotNull(result);
        verify(stopService).getObject(1L); // origin
        verify(stopService).getObject(3L); // destination

        // Verify that addOrigin and addDestination were called
        assertEquals(originStop, route.getOriginStop());
        assertEquals(destinationStop, route.getDestinationStop());
    }

    @Test
    @DisplayName("Save - Debe lanzar excepción cuando origin stop no existe")
    void save_ShouldThrowException_WhenOriginStopNotFound() {
        // Arrange
        when(routeMapper.toEntity(createRequest)).thenReturn(route);
        when(stopService.getObject(1L))
                .thenThrow(new NotFoundException("Stop not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> routeService.save(createRequest));

        verify(routeMapper).toEntity(createRequest);
        verify(stopService).getObject(1L);
        verify(routeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Save - Debe lanzar excepción cuando destination stop no existe")
    void save_ShouldThrowException_WhenDestinationStopNotFound() {
        // Arrange
        when(routeMapper.toEntity(createRequest)).thenReturn(route);
        when(stopService.getObject(1L)).thenReturn(originStop);
        when(stopService.getObject(3L))
                .thenThrow(new NotFoundException("Stop not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> routeService.save(createRequest));

        verify(routeMapper).toEntity(createRequest);
        verify(stopService).getObject(1L);
        verify(stopService).getObject(3L);
        verify(routeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update - Debe actualizar una ruta exitosamente")
    void update_ShouldUpdateRouteSuccessfully() {
        // Arrange
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(stopService.getObject(1L)).thenReturn(originStop);
        when(stopService.getObject(3L)).thenReturn(destinationStop);
        doNothing().when(routeMapper).updateEntity(updateRequest, route);
        when(routeRepository.save(any(Route.class))).thenReturn(route);
        when(routeMapper.toResponse(route)).thenReturn(response);

        // Act
        RouteDTO.routeResponse result = routeService.update(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(response.id(), result.id());

        verify(routeRepository).findById(1L);
        verify(routeMapper).updateEntity(updateRequest, route);
        verify(stopService).getObject(3L); // destination
        verify(stopService).getObject(1L); // origin
        verify(routeRepository).save(route);
        verify(routeMapper).toResponse(route);
    }

    @Test
    @DisplayName("Update - Debe lanzar excepción cuando origin y destination son iguales")
    void update_ShouldThrowException_WhenOriginAndDestinationAreSame() {
        // Arrange
        RouteDTO.routeUpdateRequest sameStopsUpdate =
                new RouteDTO.routeUpdateRequest(
                        "Ruta Test",
                        100,
                        120,
                        1L, // Same as destinationId
                        1L  // Same as originId
                );

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        doNothing().when(routeMapper).updateEntity(sameStopsUpdate, route);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> routeService.update(1L, sameStopsUpdate)
        );

        assertEquals("origen y destino no pueden ser iguales", exception.getMessage());
        verify(routeRepository).findById(1L);
        verify(routeMapper).updateEntity(sameStopsUpdate, route);
        verify(routeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update - Debe actualizar solo destination cuando solo destinationId cambia")
    void update_ShouldUpdateOnlyDestination_WhenOnlyDestinationIdChanges() {
        // Arrange
        RouteDTO.routeUpdateRequest destinationOnlyUpdate =
                new RouteDTO.routeUpdateRequest(
                        "Ruta Test",
                        100,
                        120,
                        null, // No origin change
                        4L    // New destinationId
                );

        Stop newDestinationStop = Stop.builder()
                .id(4L)
                .name("Terminal Cartagena")
                .originRoutes(new HashSet<>())
                .destinationRoutes(new HashSet<>())
                .build();

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(stopService.getObject(4L)).thenReturn(newDestinationStop);
        doNothing().when(routeMapper).updateEntity(destinationOnlyUpdate, route);
        when(routeRepository.save(any(Route.class))).thenReturn(route);
        when(routeMapper.toResponse(route)).thenReturn(response);

        // Act
        RouteDTO.routeResponse result = routeService.update(1L, destinationOnlyUpdate);

        // Assert
        assertNotNull(result);

        verify(routeRepository).findById(1L);
        verify(routeMapper).updateEntity(destinationOnlyUpdate, route);
        verify(stopService).getObject(4L);
        verify(stopService, never()).getObject(1L);
        verify(routeRepository).save(route);
    }

    @Test
    @DisplayName("Update - Debe actualizar solo origin cuando solo originId cambia")
    void update_ShouldUpdateOnlyOrigin_WhenOnlyOriginIdChanges() {
        // Arrange
        RouteDTO.routeUpdateRequest originOnlyUpdate =
                new RouteDTO.routeUpdateRequest(
                        "Ruta Test",
                        100,
                        120,
                        5L,   // New originId
                        null  // No destination change
                );

        Stop newOriginStop = Stop.builder()
                .id(5L)
                .name("Terminal Rodadero")
                .originRoutes(new HashSet<>())
                .destinationRoutes(new HashSet<>())
                .build();

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(stopService.getObject(5L)).thenReturn(newOriginStop);
        doNothing().when(routeMapper).updateEntity(originOnlyUpdate, route);
        when(routeRepository.save(any(Route.class))).thenReturn(route);
        when(routeMapper.toResponse(route)).thenReturn(response);

        // Act
        RouteDTO.routeResponse result = routeService.update(1L, originOnlyUpdate);

        // Assert
        assertNotNull(result);

        verify(routeRepository).findById(1L);
        verify(routeMapper).updateEntity(originOnlyUpdate, route);
        verify(stopService).getObject(5L);
        verify(stopService, never()).getObject(3L);
        verify(routeRepository).save(route);
    }

    @Test
    @DisplayName("Update - Debe actualizar solo campos básicos sin cambiar stops")
    void update_ShouldUpdateOnlyBasicFields_WithoutChangingStops() {
        // Arrange
        RouteDTO.routeUpdateRequest basicFieldsUpdate =
                new RouteDTO.routeUpdateRequest(
                        "Ruta Actualizada",
                        150,
                        130,
                        null, // No origin change
                        null  // No destination change
                );

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        doNothing().when(routeMapper).updateEntity(basicFieldsUpdate, route);
        when(routeRepository.save(any(Route.class))).thenReturn(route);
        when(routeMapper.toResponse(route)).thenReturn(response);

        // Act
        RouteDTO.routeResponse result = routeService.update(1L, basicFieldsUpdate);

        // Assert
        assertNotNull(result);

        verify(routeRepository).findById(1L);
        verify(routeMapper).updateEntity(basicFieldsUpdate, route);
        verify(stopService, never()).getObject(anyLong());
        verify(routeRepository).save(route);
    }

    @Test
    @DisplayName("Update - Debe lanzar excepción cuando la ruta no existe")
    void update_ShouldThrowException_WhenRouteNotFound() {
        // Arrange
        when(routeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> routeService.update(999L, updateRequest)
        );

        assertEquals("Route not found", exception.getMessage());
        verify(routeRepository).findById(999L);
        verify(routeMapper, never()).updateEntity(any(), any());
    }

    @Test
    @DisplayName("Get - Debe obtener una ruta por ID")
    void get_ShouldReturnRoute_WhenIdExists() {
        // Arrange
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(routeMapper.toResponse(route)).thenReturn(response);

        // Act
        RouteDTO.routeResponse result = routeService.get(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("RT-001", result.code());
        assertEquals("Santa Marta", result.origin());
        assertEquals("Barranquilla", result.destination());
        assertEquals(3, result.stops().size());

        verify(routeRepository).findById(1L);
        verify(routeMapper).toResponse(route);
    }

    @Test
    @DisplayName("Get - Debe lanzar excepción cuando el ID no existe")
    void get_ShouldThrowException_WhenIdNotFound() {
        // Arrange
        when(routeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> routeService.get(999L)
        );

        assertEquals("Route not found", exception.getMessage());
        verify(routeRepository).findById(999L);
        verify(routeMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("GetAll - Debe retornar página de rutas")
    void getAll_ShouldReturnPageOfRoutes() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Route> routes = List.of(route);
        Page<Route> routePage = new PageImpl<>(routes, pageable, 1);

        when(routeRepository.findAll(pageable)).thenReturn(routePage);
        when(routeMapper.toResponse(route)).thenReturn(response);

        // Act
        Page<RouteDTO.routeResponse> result = routeService.getAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(response.id(), result.getContent().get(0).id());
        assertEquals(response.code(), result.getContent().get(0).code());

        verify(routeRepository).findAll(pageable);
        verify(routeMapper).toResponse(route);
    }

    @Test
    @DisplayName("GetAll - Debe retornar página vacía cuando no hay rutas")
    void getAll_ShouldReturnEmptyPage_WhenNoRoutes() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Route> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(routeRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act
        Page<RouteDTO.routeResponse> result = routeService.getAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(routeRepository).findAll(pageable);
        verify(routeMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Save - Debe guardar ruta con código único")
    void save_ShouldSaveRouteWithUniqueCode() {
        // Arrange
        when(routeMapper.toEntity(createRequest)).thenReturn(route);
        when(stopService.getObject(1L)).thenReturn(originStop);
        when(stopService.getObject(3L)).thenReturn(destinationStop);
        when(routeRepository.save(any(Route.class))).thenReturn(route);
        when(routeMapper.toResponse(route)).thenReturn(response);

        // Act
        RouteDTO.routeResponse result = routeService.save(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals("RT-001", result.code());

        verify(routeRepository).save(route);
    }

    @Test
    @DisplayName("Save - Debe guardar ruta con distancia y duración válidas")
    void save_ShouldSaveRouteWithValidDistanceAndDuration() {
        // Arrange
        when(routeMapper.toEntity(createRequest)).thenReturn(route);
        when(stopService.getObject(1L)).thenReturn(originStop);
        when(stopService.getObject(3L)).thenReturn(destinationStop);
        when(routeRepository.save(any(Route.class))).thenReturn(route);
        when(routeMapper.toResponse(route)).thenReturn(response);

        // Act
        RouteDTO.routeResponse result = routeService.save(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(100, result.distanceKm());
        assertEquals(120, result.durationMin());
        assertTrue(result.distanceKm() > 0);
        assertTrue(result.durationMin() > 0);
    }

    @Test
    @DisplayName("Get - Debe retornar ruta con lista de stops en orden")
    void get_ShouldReturnRouteWithStopsInOrder() {
        // Arrange
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(routeMapper.toResponse(route)).thenReturn(response);

        // Act
        RouteDTO.routeResponse result = routeService.get(1L);

        // Assert
        assertNotNull(result);
        assertNotNull(result.stops());
        assertEquals(3, result.stops().size());

        // Verify stops are in order
        assertEquals(1, result.stops().get(0).order());
        assertEquals(2, result.stops().get(1).order());
        assertEquals(3, result.stops().get(2).order());

        assertEquals("Terminal Santa Marta", result.stops().get(0).name());
        assertEquals("Terminal Ciénaga", result.stops().get(1).name());
        assertEquals("Terminal Barranquilla", result.stops().get(2).name());
    }

    @Test
    @DisplayName("Update - Debe actualizar ambos stops cuando ambos IDs cambian")
    void update_ShouldUpdateBothStops_WhenBothIdsChange() {
        // Arrange
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(stopService.getObject(1L)).thenReturn(originStop);
        when(stopService.getObject(3L)).thenReturn(destinationStop);
        doNothing().when(routeMapper).updateEntity(updateRequest, route);
        when(routeRepository.save(any(Route.class))).thenReturn(route);
        when(routeMapper.toResponse(route)).thenReturn(response);

        // Act
        RouteDTO.routeResponse result = routeService.update(1L, updateRequest);

        // Assert
        assertNotNull(result);

        verify(stopService).getObject(1L); // origin
        verify(stopService).getObject(3L); // destination
        verify(routeRepository).save(route);
    }
}