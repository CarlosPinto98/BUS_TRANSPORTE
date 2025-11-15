package com.unimag.service;

import com.unimag.DTO.StopDTO;
import com.unimag.entities.City;
import com.unimag.entities.Route;
import com.unimag.entities.Stop;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.StopMapper;
import com.unimag.repository.StopRepository;
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

import static org.mockito.ArgumentMatchers.any;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StopServiceImplTest {

    @Mock
    private StopRepository stopRepository;

    @Mock
    private StopMapper stopMapper;

    @Mock
    private CityServiceImpl cityService;

    @InjectMocks
    private StopServiceImpl stopService;

    private Stop stop;
    private Route route;
    private City city;
    private StopDTO.stopCreateRequest createRequest;
    private StopDTO.stopUpdateRequest updateRequest;
    private StopDTO.stopResponse response;

    @BeforeEach
    void setUp() {

        route = Route.builder()
                .id(1L)
                .code("RT-001")
                .name("Ruta Santa Marta - Barranquilla")
                .origin("Santa Marta")
                .destination("Barranquilla")
                .build();

        city = City.builder()
                .id(1L)
                .name("Santa Marta")
                .lat(11.2408f)
                .lon(-74.2120f)
                .stops(new HashSet<>())
                .build();

        stop = Stop.builder()
                .id(1L)
                .name("Terminal Santa Marta")
                .order(1)
                .lat(BigDecimal.valueOf(11.2408))
                .lng(BigDecimal.valueOf(-74.2120))
                .route(route)
                .city(city)
                .originRoutes(new HashSet<>())
                .destinationRoutes(new HashSet<>())
                .build();

        createRequest = new StopDTO.stopCreateRequest(
                "Terminal Santa Marta",     // name
                1,                          // order
                BigDecimal.valueOf(11.2408), // lat
                BigDecimal.valueOf(-74.2120), // lng
                1L,                         // routeId
                1L                          // cityId
        );

        updateRequest = new StopDTO.stopUpdateRequest(
                "Terminal Santa Marta Centro", // name
                2,                             // order
                1L                             // cityId
        );

        StopDTO.cityDTO cityDTO = new StopDTO.cityDTO("Santa Marta");

        response = new StopDTO.stopResponse(
                1L,
                "Terminal Santa Marta",
                1,
                BigDecimal.valueOf(11.2408),
                BigDecimal.valueOf(-74.2120),
                1L,
                "Ruta Santa Marta - Barranquilla",
                "RT-001",
                cityDTO
        );
    }

    @Test
    @DisplayName("Save - Debe guardar una parada exitosamente")
    void save() {

        when(stopMapper.toEntity(createRequest)).thenReturn(stop);
        when(cityService.getObject(1L)).thenReturn(city);
        when(stopRepository.save(stop)).thenReturn(stop);
        when(stopMapper.toResponse(stop)).thenReturn(response);

        StopDTO.stopResponse result = stopService.save(createRequest);

        assertNotNull(result);
        assertEquals(response.id(), result.id());
        assertEquals(response.name(), result.name());
        assertEquals(response.order(), result.order());

        verify(stopMapper).toEntity(createRequest);
        verify(cityService).getObject(1L);
        verify(stopRepository).save(stop);
        verify(stopMapper).toResponse(stop);
    }

    @Test
    @DisplayName("Save - Debe lanzar excepción cuando la ciudad no existe")
    void save_ShouldThrowException_WhenCityNotFound() {

        when(stopMapper.toEntity(createRequest)).thenReturn(stop);
        when(cityService.getObject(1L))
                .thenThrow(new NotFoundException("City not found"));

        assertThrows(NotFoundException.class, () -> stopService.save(createRequest));

        verify(stopMapper).toEntity(createRequest);
        verify(cityService).getObject(1L);
        verify(stopRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get by ID - Debe obtener una parada por ID")
    void get() {

        when(stopRepository.findById(1L)).thenReturn(Optional.of(stop));
        when(stopMapper.toResponse(stop)).thenReturn(response);

        StopDTO.stopResponse result = stopService.get(1L);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.id());
        assertEquals("Terminal Santa Marta", result.name());
        assertEquals(Integer.valueOf(1), result.order());

        verify(stopRepository).findById(1L);
        verify(stopMapper).toResponse(stop);
    }

    @Test
    @DisplayName("Get by ID - Debe lanzar excepción cuando el ID no existe")
    void get_ShouldThrowException_WhenIdNotFound() {

        when(stopRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> stopService.get(999L)
        );

        assertEquals("Stop not found", exception.getMessage());
        verify(stopRepository).findById(999L);
        verify(stopMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Get by Name - Debe obtener una parada por nombre (ignore case)")
    void testGet() {

        String name = "terminal santa marta";
        when(stopRepository.findByNameIgnoreCase(name)).thenReturn(Optional.of(stop));
        when(stopMapper.toResponse(stop)).thenReturn(response);

        StopDTO.stopResponse result = stopService.get(name);

        assertNotNull(result);
        assertEquals("Terminal Santa Marta", result.name());

        verify(stopRepository).findByNameIgnoreCase(name);
        verify(stopMapper).toResponse(stop);
    }

    @Test
    @DisplayName("Get by Name - Debe lanzar excepción cuando el nombre no existe")
    void testGet_ShouldThrowException_WhenNameNotFound() {

        String invalidName = "Terminal Inexistente";
        when(stopRepository.findByNameIgnoreCase(invalidName))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> stopService.get(invalidName)
        );

        assertEquals("Stop not found", exception.getMessage());
        verify(stopRepository).findByNameIgnoreCase(invalidName);
        verify(stopMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("GetAll - Debe retornar página de paradas")
    void getAll() {

        Pageable pageable = PageRequest.of(0, 10);
        List<Stop> stops = List.of(stop);
        Page<Stop> stopPage = new PageImpl<>(stops, pageable, 1);

        when(stopRepository.findAll(pageable)).thenReturn(stopPage);
        when(stopMapper.toResponse(stop)).thenReturn(response);

        Page<StopDTO.stopResponse> result = stopService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(response.id(), result.getContent().get(0).id());

        verify(stopRepository).findAll(pageable);
        verify(stopMapper).toResponse(stop);
    }

    @Test
    @DisplayName("GetObject by ID - Debe retornar entidad Stop cuando existe")
    void getObject() {

        when(stopRepository.findById(1L)).thenReturn(Optional.of(stop));

        Stop result = stopService.getObject(1L);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
        assertEquals("Terminal Santa Marta", result.getName());
        assertEquals(Integer.valueOf(1), result.getOrder());
        assertNotNull(result.getCity());
        assertEquals(Long.valueOf(1L), result.getCity().getId());

        verify(stopRepository).findById(1L);
    }

    @Test
    @DisplayName("GetObject by ID - Debe lanzar excepción cuando no existe")
    void getObject_ShouldThrowException_WhenNotExists() {

        when(stopRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> stopService.getObject(999L)
        );

        assertEquals("Stop not found", exception.getMessage());
        verify(stopRepository).findById(999L);
    }

    @Test
    @DisplayName("GetObject by Name - Debe retornar entidad Stop cuando existe")
    void testGetObject() {

        String name = "Terminal Santa Marta";
        when(stopRepository.findByNameIgnoreCase(name)).thenReturn(Optional.of(stop));

        Stop result = stopService.getObject(name);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
        assertEquals("Terminal Santa Marta", result.getName());

        verify(stopRepository).findByNameIgnoreCase(name);
    }

    @Test
    @DisplayName("Delete by ID - Debe eliminar parada cuando existe")
    void delete() {

        when(stopRepository.findById(1L)).thenReturn(Optional.of(stop));
        doNothing().when(stopRepository).delete(stop);

        boolean result = stopService.delete(1L);

        assertTrue(result);
        verify(stopRepository).findById(1L);
        verify(stopRepository).delete(stop);
    }

    @Test@DisplayName("Delete by Name - Debe eliminar parada cuando existe")
    void testDelete() {

        String name = "Terminal Santa Marta";
        when(stopRepository.findByNameIgnoreCase(name)).thenReturn(Optional.of(stop));
        doNothing().when(stopRepository).delete(stop);

        boolean result = stopService.delete(name);

        assertTrue(result);
        verify(stopRepository).findByNameIgnoreCase(name);
        verify(stopRepository).delete(stop);
    }

//    @Test
//    @DisplayName("UpdateStop - Debe actualizar parada exitosamente")
//    void updateStop() {
//
//        when(stopRepository.findById(1L)).thenReturn(Optional.of(stop));
//        when(cityService.getObject(1L)).thenReturn(city);
//        doNothing().when(stopMapper).updateEntity(updateRequest, stop);
//        when(stopMapper.toResponse(stop)).thenReturn(response);
//
//        StopDTO.stopResponse result = stopService.updateStop(updateRequest, 1L);
//
//        assertNotNull(result);
//        assertEquals(response.id(), result.id());
//
//        verify(stopRepository).findById(1L);
//        verify(stopMapper).updateEntity(updateRequest, stop);
//        verify(stopMapper).toResponse(stop);
//    }

    @Test
    @DisplayName("Delete by Name - Debe lanzar excepción cuando el nombre no existe")
    void testDelete_ShouldThrowException_WhenNameNotFound() {

        String invalidName = "Terminal Inexistente";
        when(stopRepository.findByNameIgnoreCase(invalidName))
                .thenReturn(Optional.empty());


        assertThrows(NotFoundException.class, () -> stopService.delete(invalidName));

        verify(stopRepository).findByNameIgnoreCase(invalidName);
        verify(stopRepository, never()).delete(any());
    }
}