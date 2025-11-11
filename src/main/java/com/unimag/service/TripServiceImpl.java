package com.unimag.service;

import com.unimag.DTO.TripDTO;
import com.unimag.entities.Trip;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.TripMapper;
import com.unimag.repository.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor

public class TripServiceImpl implements TripService {

    @Autowired
    private final TripRepository tripRepository;

    @Autowired
    private final TripMapper tripMapper;

    @Override
    public TripDTO.tripResponse save(TripDTO.tripCreateRequest tripDTO) {
        return null;
    }

    @Override
    public TripDTO.tripResponse update(TripDTO.tripUpdateRequest tripDTO, Trip trip) {
        return null;
    }

    @Override
    public boolean delete(Long tripId) {
        return false;
    }

    @Override
    public TripDTO.tripResponse get(Long id) {
        return null;
    }

    @Override
    public Page<TripDTO.tripResponse> getAll(Pageable pageable) {
        return null;
    }

    @Override
    public Trip getObject(Long id) {
        return tripRepository.findById(id).orElseThrow(() -> new NotFoundException("Trip not found"));
    }
}
