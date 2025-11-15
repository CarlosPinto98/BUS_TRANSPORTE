package com.unimag.service;

import com.unimag.DTO.*;

import java.util.List;

public interface RouteService {

    RouteDTO.routeResponse createRoute(RouteDTO.routeCreateRequest request);
    RouteDTO.routeResponse updateRoute(Long id, RouteDTO.routeUpdateRequest request);
    RouteDTO.routeResponse getRouteById(Long id);
    RouteDTO.routeResponse getRouteByCode(String code);
    RouteDTO.routeResponse getRouteWithStops(Long id);
    List<RouteDTO.routeResponse> getAllRoutes();
    List<RouteDTO.routeResponse> searchRoutes(String origin, String destination);
    List<StopDTO.stopResponse> getStopsByRoute(Long routeId);
    void deleteRoute(Long id);
    boolean existsByCode(String code);
}
