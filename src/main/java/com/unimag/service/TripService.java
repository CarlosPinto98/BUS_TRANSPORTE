package com.unimag.service;

import com.unimag.DTO.TripDTO.*;
import com.unimag.entities.Enums.StatusTrip;
import com.unimag.entities.Trip;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TripService {

    tripResponse createTrip(tripCreateRequest request);
    tripResponse updateTrip(Long id, tripUpdateRequest request);
    tripResponse getTripById(Long id);
    tripResponse getTripWithDetails(Long id);
    List<tripResponse> getAllTrips();
    List<tripResponse> getTripsByRouteAndDate(Long routeId, LocalDate date);
    List<tripResponse> getTripsByRouteAndDateAndStatus(Long routeId, LocalDate date, StatusTrip status);
    List<tripResponse> searchTrips(Long routeId, LocalDate date, StatusTrip status);
    List<tripResponse> getTripsByDateAndStatus(LocalDate date, StatusTrip status);
    List<tripResponse> getActiveTripsByBus(Long busId, LocalDate date);
    void deleteTrip(Long id);
    tripResponse changeTripStatus(Long id, StatusTrip status);
    void validateTripSchedule(Long busId, LocalDate date, LocalDateTime departureAt);
    Trip getObject(Long id);


//    TripDTO.tripResponse save(TripDTO.tripCreateRequest tripDTO);
//    TripDTO.tripResponse update(TripDTO.tripUpdateRequest tripDTO, Trip trip);
//    boolean delete(Long tripId);
//    TripDTO.tripResponse get(Long id);
//    Page<TripDTO.tripResponse> getAll(Pageable pageable);



}
