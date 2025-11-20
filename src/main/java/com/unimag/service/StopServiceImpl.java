package com.unimag.service;

import com.unimag.DTO.StopDTO;
import com.unimag.entities.Route;
import com.unimag.entities.Stop;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.StopMapper;
import com.unimag.repository.RouteRepository;
import com.unimag.repository.StopRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor

public class StopServiceImpl implements StopService {


    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;
    private final StopMapper stopMapper;
    private final CityServiceImpl cityService;

    @Override
    public StopDTO.stopResponse create(StopDTO.stopCreateRequest request) {
        Route route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new IllegalArgumentException("Route not found: " + request.routeId()));

        validateStopOrder(request.routeId(), request.order());

        Stop stop = stopMapper.toEntity(request);
        stop.setRoute(route);

        Stop savedStop = stopRepository.save(stop);
        return stopMapper.toResponse(savedStop);
    }

    @Override
    public StopDTO.stopResponse updateStop(Long id, StopDTO.stopUpdateRequest request) {
        Stop stop = stopRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stop not found: " + id));

        if (!stop.getOrder().equals(request.order())) {
            validateStopOrder(stop.getRoute().getId(), request.order());
        }

        stopMapper.updateEntity(request, stop);
        Stop updatedStop = stopRepository.save(stop);
        return stopMapper.toResponse(updatedStop);
    }

    @Override
    public StopDTO.stopResponse getStopById(Long id) {
        Stop stop = stopRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stop not found: " + id));
        return stopMapper.toResponse(stop);
    }

    @Override
    public List<StopDTO.stopResponse> getAllStops() {
        return stopRepository.findAll().stream()
                .map(stopMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StopDTO.stopResponse> getStopsByRouteId(Long routeId) {
        if (!routeRepository.existsById(routeId)) {
            throw new IllegalArgumentException("Route not found: " + routeId);
        }
        return stopRepository.findByRouteIdOrderByOrderAsc(routeId).stream()
                .map(stopMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StopDTO.stopResponse> searchStopsByName(String name) {
        return stopRepository.findByNameContainingIgnoreCase(name).stream()
                .map(stopMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StopDTO.stopResponse save(StopDTO.stopCreateRequest createRequest) {
        var s = stopMapper.toEntity(createRequest);
        s.addCity(cityService.getObject(createRequest.cityId()));
        return stopMapper.toResponse(stopRepository.save(s));
    }

    @Override
    public StopDTO.stopResponse get(Long id) {
        return stopMapper.toResponse(getObject(id));
    }

    @Override
    public StopDTO.stopResponse get(String name) {
        return stopMapper.toResponse(getObject(name));
    }

    @Override
    public Page<StopDTO.stopResponse> getAll(Pageable pageable) {
        return stopRepository.findAll(pageable).map(stopMapper::toResponse);
    }

    @Override
    public Stop getObject(Long id) {
        var s = stopRepository.findById(id).orElseThrow(() -> new NotFoundException("Stop not found"));
        return s;
    }

    @Override
    public Stop getObject(String name) {
        var s = stopRepository.findByNameIgnoreCase(name).orElseThrow(() -> new NotFoundException("Stop not found"));
        return s;
    }

    @Override
    public boolean delete(Long id) {
        var s = getObject(id);
        stopRepository.delete(s);
        return true;
    }

    @Override
    public boolean delete(String name) {
        var s = getObject(name);
        stopRepository.delete(s);
        return true;
    }

    @Override
    public StopDTO.stopResponse updateStop(StopDTO.stopUpdateRequest stopDTO, Long id) {
        var s = getObject(id);
        var city = s.getCity();
        stopMapper.updateEntity(stopDTO, s);
        if (!city.getId().equals(stopDTO.cityId()) && stopDTO.cityId() != null) {
            s.removeCity(city);
            s.addCity(cityService.getObject(stopDTO.cityId()));
        }
        return stopMapper.toResponse(s);
    }

    public void validateStopOrder(Long routeId, Integer order) {
        if (order < 0) {
            throw new IllegalArgumentException("Stop order must be non-negative");
        }

        List<Stop> existingStops = stopRepository.findByRouteIdOrderByOrderAsc(routeId);
        boolean orderExists = existingStops.stream()
                .anyMatch(stop -> stop.getOrder().equals(order));

        if (orderExists) {
            throw new IllegalArgumentException("Stop order " + order + " already exists for route " + routeId);
        }
    }
}
