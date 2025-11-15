package com.unimag.service;

import com.unimag.DTO.SeatHoldDTO;
import com.unimag.entities.SeatHold;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SeatHoldService {

    SeatHoldDTO.seatHoldResponse create(SeatHoldDTO.seatHoldCreateRequest request, Long userId);
    SeatHoldDTO.seatHoldResponse save(SeatHoldDTO.seatHoldCreateRequest seatHoldCreateRequest);
    SeatHoldDTO.seatHoldResponse get(Long id);
    List<SeatHoldDTO.seatHoldResponse> getAll(Pageable pageable);
    void delete(Long id);
    SeatHoldDTO.seatHoldResponse update(SeatHoldDTO.seatHoldUpdateRequest seatHoldUpdateRequest, Long id);
    SeatHold getObject(Long id);

    SeatHoldDTO.seatHoldResponse getSeatHoldById(Long id);
    List<SeatHoldDTO.seatHoldResponse> getSeatHoldsByTripId(Long tripId);
    List<SeatHoldDTO.seatHoldResponse> getSeatHoldsByUserId(Long userId);
    List<SeatHoldDTO.seatHoldResponse> getActiveSeatHoldsByTrip(Long tripId);

    void expireOldHolds();
    boolean isSeatHeld(Long tripId, String seatNumber);
    void releaseSeatHold(Long holdId);
    void convertHoldToTicket(Long holdId);
}
