package com.unimag.service;

import com.unimag.DTO.TripDTO;
import com.unimag.entities.SeatHold;
import com.unimag.entities.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TripService {

    TripDTO.tripResponse save(TripDTO.tripCreateRequest tripDTO);
    TripDTO.tripResponse update(TripDTO.tripUpdateRequest tripDTO, Long trip);
    void delete(Long tripId);
    TripDTO.tripResponse get(Long id);
    Page<TripDTO.tripResponse> getAll(Pageable pageable);
    Trip getObject(Long id);

    // hay que colocar sus queries

//    Page<TripDTO.tripResponse> getTripsByOriginAndDestination(Pageable pageable, String origin, String destination);
//    List<Integer> findSeatsHold(Long tripId);
//    List<SeatHold> findUnpaidSeatsHold(Long tripId);
}
