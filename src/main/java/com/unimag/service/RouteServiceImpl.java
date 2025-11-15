package com.unimag.service;

import com.unimag.DTO.RouteDTO;
import com.unimag.DTO.StopDTO;
import com.unimag.entities.Route;
import com.unimag.mappers.RouteMapper;
import com.unimag.mappers.StopMapper;
import com.unimag.repository.RouteRepository;
import com.unimag.repository.StopRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor

public class RouteServiceImpl implements RouteService {


    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;
    private final RouteMapper routeMapper;
    private final StopMapper stopMapper;

    @Override
    public RouteDTO.routeResponse createRoute(RouteDTO.routeCreateRequest request) {
        if (routeRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Route code already exists: " + request.code());
        }

        Route route = routeMapper.toEntity(request);
        Route savedRoute = routeRepository.save(route);
        return routeMapper.toResponse(savedRoute);
    }

    @Override
    public RouteDTO.routeResponse updateRoute(Long id, RouteDTO.routeUpdateRequest request) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Route not found: " + id));

        routeMapper.updateEntity(request, route);
        Route updatedRoute = routeRepository.save(route);
        return routeMapper.toResponse(updatedRoute);
    }

    @Override
    public RouteDTO.routeResponse getRouteById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Route not found: " + id));
        return routeMapper.toResponse(route);
    }

    @Override
    public RouteDTO.routeResponse getRouteByCode(String code) {
        Route route = routeRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Route not found with code: " + code));
        return routeMapper.toResponse(route);
    }

    @Override
    public RouteDTO.routeResponse getRouteWithStops(Long id) {
        Route route = routeRepository.findByIdWithStops(id)
                .orElseThrow(() -> new IllegalArgumentException("Route not found: " + id));
        return routeMapper.toResponse(route);
    }

    @Override
    public List<RouteDTO.routeResponse> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(routeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RouteDTO.routeResponse> searchRoutes(String origin, String destination) {
        if (origin != null && destination != null) {
            return routeRepository.findByOriginAndDestination(origin, destination).stream()
                    .map(routeMapper::toResponse)
                    .collect(Collectors.toList());
        } else if (origin != null || destination != null) {
            String searchTerm = origin != null ? origin : destination;
            return routeRepository.findByOriginContainingIgnoreCaseOrDestinationContainingIgnoreCase(
                            searchTerm, searchTerm).stream()
                    .map(routeMapper::toResponse)
                    .collect(Collectors.toList());
        }
        return getAllRoutes();
    }

    @Override
    public List<StopDTO.stopResponse> getStopsByRoute(Long routeId) {
        if (!routeRepository.existsById(routeId)) {
            throw new IllegalArgumentException("Route not found: " + routeId);
        }
        return stopRepository.findByRouteIdOrderByOrderAsc(routeId).stream()
                .map(stopMapper::toResponse)
                .collect(Collectors.toList()).reversed();
    }

    @Override
    public void deleteRoute(Long id) {
        if (!routeRepository.existsById(id)) {
            throw new IllegalArgumentException("Route not found: " + id);
        }
        routeRepository.deleteById(id);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public boolean existsByCode(String code) {
        return routeRepository.existsByCode(code);
    }
}
