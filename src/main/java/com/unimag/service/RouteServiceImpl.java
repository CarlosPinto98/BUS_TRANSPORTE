package com.unimag.service;

import com.unimag.DTO.RouteDTO;
import com.unimag.entities.Route;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.RouteMapper;
import com.unimag.repository.RouteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor

public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;
    private final StopService stopService;

    @Override
    public void delete(Long id) {
        routeRepository.deleteById(id);
    }

    @Override
    public Route getObject(Long id) {
        return routeRepository.findById(id).orElseThrow(()-> new NotFoundException("Route not found"));
    }

    @Override
    public RouteDTO.routeResponse save(RouteDTO.routeCreateRequest createRequest) {
        var route = routeMapper.toEntity(createRequest);
        var origin = stopService.getObject(createRequest.originId());
        var destination = stopService.getObject(createRequest.destinationId());
        route.addOrigin(origin);
        route.addDestination(destination);
        return routeMapper.toResponse(routeRepository.save(route));
    }

    @Override
    public RouteDTO.routeResponse update(Long id, RouteDTO.routeUpdateRequest updateRequest) {
        var route = getObject(id);
        routeMapper.updateEntity(updateRequest, route);

        if (updateRequest.originId() != null && updateRequest.destinationId() != null
                && updateRequest.originId().equals(updateRequest.destinationId())) {
            throw new IllegalArgumentException("origen y destino no pueden ser iguales");
        }

        if( updateRequest.destinationId()!=null ){
            route.addDestination(stopService.getObject(updateRequest.destinationId()));
        }

        if( updateRequest.originId()!=null ){
            route.addOrigin(stopService.getObject(updateRequest.originId()));
        }
        return routeMapper.toResponse(routeRepository.save(route));
    }

    @Override
    public RouteDTO.routeResponse get(Long id) {
        return routeMapper.toResponse(getObject(id));
    }

    @Override
    public Page<RouteDTO.routeResponse> getAll(Pageable pageable) {
        return routeRepository.findAll(pageable).map(routeMapper::toResponse);
    }
}
