package com.unimag.service;

import com.unimag.DTO.SeatHoldDTO;
import com.unimag.entities.SeatHold;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SeatHoldService {

    SeatHoldDTO.seatHoldResponse save(SeatHoldDTO.seatHoldCreateRequest seatHoldCreateRequest);
    SeatHoldDTO.seatHoldResponse get(Long id);
    Page<SeatHoldDTO.seatHoldResponse> getAll(Pageable pageable);
    void delete(Long id);
    SeatHoldDTO.seatHoldResponse update(SeatHoldDTO.seatHoldUpdateRequest seatHoldUpdateRequest, Long id);
    SeatHold getObject(Long id);
}
