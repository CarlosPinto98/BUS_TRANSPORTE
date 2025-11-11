package com.unimag.service;

import com.unimag.DTO.RouteDTO;
import com.unimag.entities.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RouteService {

    void delete(Long id);
    Route getObject (Long id);
    RouteDTO.routeResponse save(RouteDTO.routeCreateRequest request);
    RouteDTO.routeResponse update(Long id, RouteDTO.routeUpdateRequest request);
    RouteDTO.routeResponse get(Long id);
    Page<RouteDTO.routeResponse> getAll(Pageable pageable);
    //RouteDTO.routeResponse getRouteByOriginAndDestination(Stop origin, Stop destination);
}
