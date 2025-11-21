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

        originStop = Stop.builder()
                .id(1L)
                .name("Terminal Santa Marta")
                .order(1)
                .lat(BigDecimal.valueOf(11.2408))
                .lng(BigDecimal.valueOf(-74.2120))
                .originRoutes(new HashSet<>())
                .destinationRoutes(new HashSet<>())
                .build();

        intermediateStop = Stop.builder()
                .id(2L)
                .name("Terminal Ciénaga")
                .order(2)
                .lat(BigDecimal.valueOf(11.0070))
                .lng(BigDecimal.valueOf(-74.2493))
                .build();


        destinationStop = Stop.builder()
                .id(3L)
                .name("Terminal Barranquilla")
                .order(3)
                .lat(BigDecimal.valueOf(10.9685))
                .lng(BigDecimal.valueOf(-74.7813))
                .originRoutes(new HashSet<>())
                .destinationRoutes(new HashSet<>())
                .build();


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

        stopSummaries = List.of(
                new RouteDTO.stopSummary(1L, "Terminal Santa Marta", 1,
                        BigDecimal.valueOf(11.2408), BigDecimal.valueOf(-74.2120)),
                new RouteDTO.stopSummary(2L, "Terminal Ciénaga", 2,
                        BigDecimal.valueOf(11.0070), BigDecimal.valueOf(-74.2493)),
                new RouteDTO.stopSummary(3L, "Terminal Barranquilla", 3,
                        BigDecimal.valueOf(10.9685), BigDecimal.valueOf(-74.7813))
        );


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
                115              // durationMin
  //              1L,                 // originId
//                3L                  // destinationId
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


}