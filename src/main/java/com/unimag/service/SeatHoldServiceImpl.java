package com.unimag.service;

import com.unimag.DTO.SeatHoldDTO;
import com.unimag.entities.Enums.StatusSeatHold;
import com.unimag.entities.SeatHold;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.SeatHoldMapper;
import com.unimag.repository.SeatHoldRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor

public class SeatHoldServiceImpl  implements SeatHoldService {

    private final SeatHoldRepository seatHoldRepository;
    private final SeatHoldMapper seatHoldMapper;
    private final SeatService seatService;
    private final TripService tripService;
    private final UserService userService;

    @Override
    public SeatHoldDTO.seatHoldResponse save(SeatHoldDTO.seatHoldCreateRequest seatHoldCreateRequest) {
        return null;
    }

    @Override
    public SeatHoldDTO.seatHoldResponse get(Long id) {
        return null;
    }

    @Override
    public Page<SeatHoldDTO.seatHoldResponse> getAll(Pageable pageable) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public SeatHoldDTO.seatHoldResponse update(SeatHoldDTO.seatHoldUpdateRequest updateRequest, Long id) {

        var s = getObject(id);
        seatHoldMapper.updateEntity(updateRequest, s);

        if (updateRequest.userId() != null) {
            s.setUser(userService.getObject(updateRequest.userId()));
        }
        if (updateRequest.tripId() != null) {
            s.setTrip(tripService.getObject(updateRequest.tripId()));
        }
        if (updateRequest.seatId() != null) {
            s.setSeat(seatService.getObject(updateRequest.seatId()));
        }
        return seatHoldMapper.toResponse(seatHoldRepository.save(s));
    }

    @Override
    public SeatHold getObject(Long id) {
        return seatHoldRepository.findById(id).orElseThrow(() -> new NotFoundException("SeatHold not found"));
    }

    public boolean isSeatfree(Long seatId, Long tripId) {
        var s = seatHoldRepository.findByTripIdAndStatusSeatHold(seatId, StatusSeatHold.HOLD);
        if (s.isEmpty()) {
            return true;
        }
        throw new IllegalArgumentException("Seat is not free");
    }
}
