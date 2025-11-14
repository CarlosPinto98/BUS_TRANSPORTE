package com.unimag.service;

import com.unimag.DTO.SeatDTO;
import com.unimag.entities.Seat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SeatService {

    SeatDTO.seatResponse save(SeatDTO.seatCreateRequest seatDTO);
    SeatDTO.seatResponse update(SeatDTO.seatUpdateRequest seatUpdateRequest, Long id);
    void delete(Long id);
    SeatDTO.seatResponse get(Long id);
    Page<SeatDTO.seatResponse> getAll(Pageable pageable);
    Seat getSeatByNumberAndBusId(int number, Long busId);
    Seat getObject(Long id);
}
