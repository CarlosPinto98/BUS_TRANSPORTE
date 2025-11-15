package com.unimag.service;

import com.unimag.DTO.SeatHoldDTO;
import com.unimag.entities.Enums.StatusSeatHold;
import com.unimag.entities.SeatHold;
import com.unimag.entities.Trip;
import com.unimag.entities.User;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.SeatHoldMapper;
import com.unimag.repository.SeatHoldRepository;
import com.unimag.repository.TripRepository;
import com.unimag.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor

public class SeatHoldServiceImpl  implements SeatHoldService {


    private final SeatHoldRepository seatHoldRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final SeatHoldMapper seatHoldMapper;

    private static final int HOLD_DURATION_MINUTES = 10;

    @Override
    public SeatHoldDTO.seatHoldResponse create(SeatHoldDTO.seatHoldCreateRequest request, Long userId) {
        Trip trip = tripRepository.findById(request.tripId())
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + request.tripId()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (isSeatHeld(request.tripId(), request.seatNumber())) {
            throw new IllegalArgumentException("Seat " + request.seatNumber() + " is already held");
        }

        SeatHold seatHold = seatHoldMapper.toEntity(request);
        seatHold.setTrip(trip);
        seatHold.setUser(user);
        seatHold.setExpiresAt(LocalDateTime.now().plusMinutes(HOLD_DURATION_MINUTES));
        seatHold.setStatusSeatHold(StatusSeatHold.HOLD);

        SeatHold savedHold = seatHoldRepository.save(seatHold);
        return seatHoldMapper.toResponse(savedHold);
    }

    @Override
    public SeatHoldDTO.seatHoldResponse getSeatHoldById(Long id) {
        SeatHold seatHold = seatHoldRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SeatHold not found: " + id));
        return seatHoldMapper.toResponse(seatHold);
    }

    @Override
    public List<SeatHoldDTO.seatHoldResponse> getSeatHoldsByTripId(Long tripId) {
        if (!tripRepository.existsById(tripId)) {
            throw new IllegalArgumentException("Trip not found: " + tripId);
        }
        return seatHoldRepository.findByTripIdAndStatusSeatHold(tripId, StatusSeatHold.HOLD).stream()
                .map(seatHoldMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeatHoldDTO.seatHoldResponse> getSeatHoldsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        return seatHoldRepository.findByUserIdAndStatusSeatHold(userId, StatusSeatHold.HOLD).stream()
                .map(seatHoldMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void expireOldHolds() {
        List<SeatHold> expiredHolds = seatHoldRepository.findExpiredHolds(LocalDateTime.now());
        expiredHolds.forEach(hold -> {
            hold.setStatusSeatHold(StatusSeatHold.EXPIRED);
            seatHoldRepository.save(hold);
        });
    }

    @Override
    public boolean isSeatHeld(Long tripId, String seatNumber) {
        return seatHoldRepository.existsByTripIdAndSeatNumberAndStatusSeatHold(
                tripId, seatNumber, StatusSeatHold.HOLD);
    }

    @Override
    public void releaseSeatHold(Long holdId) {
        SeatHold seatHold = seatHoldRepository.findById(holdId)
                .orElseThrow(() -> new IllegalArgumentException("SeatHold not found: " + holdId));

        if (seatHold.getStatusSeatHold() != StatusSeatHold.HOLD) {
            throw new IllegalArgumentException("Can only release active seatHolds");
        }

        seatHold.setStatusSeatHold(StatusSeatHold.EXPIRED);
        seatHoldRepository.save(seatHold);
    }

    @Override
    public void convertHoldToTicket(Long holdId) {
        SeatHold seatHold = seatHoldRepository.findById(holdId)
                .orElseThrow(() -> new IllegalArgumentException("SeatHold not found: " + holdId));

        if (seatHold.getStatusSeatHold() != StatusSeatHold.HOLD) {
            throw new IllegalArgumentException("Can only convert active holds");
        }

        seatHold.setStatusSeatHold(StatusSeatHold.CONVERTED);
        seatHoldRepository.save(seatHold);
    }


    @Override
    public List<SeatHoldDTO.seatHoldResponse> getAll(Pageable pageable) {
        return seatHoldRepository.findAll().stream()
                .map(seatHoldMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!seatHoldRepository.existsById(id)) {
            throw new IllegalArgumentException("SeatHold not found: " + id);
        }
        seatHoldRepository.deleteById(id);
    }

    @Override
    public SeatHoldDTO.seatHoldResponse update(SeatHoldDTO.seatHoldUpdateRequest updateRequest, Long id) {

        SeatHold seatHold = seatHoldRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SeatHold not found: " + id));

        seatHoldMapper.updateEntity(updateRequest, seatHold);
        SeatHold updatedHold = seatHoldRepository.save(seatHold);
        return seatHoldMapper.toResponse(updatedHold);
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

        @Override
    public SeatHoldDTO.seatHoldResponse save(SeatHoldDTO.seatHoldCreateRequest seatHoldCreateRequest) {
        return null;
    }

    @Override
    public SeatHoldDTO.seatHoldResponse get(Long id) {
        return null;
    }

    @Override
    public List<SeatHoldDTO.seatHoldResponse> getActiveSeatHoldsByTrip(Long tripId) {
        return List.of();
    }
}
