package com.unimag.service;

import com.unimag.DTO.BusDTO;
import com.unimag.entities.Bus;
import com.unimag.entities.Enums.StatusBus;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.BusMapper;
import com.unimag.repository.BusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor

public class BusServiceImpl implements BusService {

    private final BusRepository busRepository;
    private final BusMapper busMapper;

    @Override
    public BusDTO.busResponse createBus(BusDTO.busCreateRequest createRequest) {
        if (busRepository.existsByPlate(createRequest.plate())) {
            throw new IllegalArgumentException("Bus plate already exists: " + createRequest.plate());
        }

        Bus bus = busMapper.toEntity(createRequest);
        Bus savedBus = busRepository.save(bus);
        return busMapper.toResponse(savedBus);
    }

    @Override
    public BusDTO.busResponse updateBus(Long id, BusDTO.busUpdateRequest updateRequest) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bus not found: " + id));

        busMapper.updateEntity(updateRequest, bus);
        Bus updatedBus = busRepository.save(bus);
        return busMapper.toResponse(updatedBus);
    }

    @Override
    public BusDTO.busResponse getBusById(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bus not found: " + id));
        return busMapper.toResponse(bus);
    }

    @Override
    public BusDTO.busResponse getBusWithSeats(Long id) {
        Bus bus = busRepository.findByIdWithSeats(id)
                .orElseThrow(() -> new IllegalArgumentException("Bus not found: " + id));
        return busMapper.toResponse(bus);
    }

    @Override
    public BusDTO.busResponse getBusbyPlate(String plate) {
        Bus bus = busRepository.findByPlate(plate)
                .orElseThrow(() -> new IllegalArgumentException("Bus not found with plate: " + plate));
        return busMapper.toResponse(bus);
    }

    @Override
    public List<BusDTO.busResponse> getAllBuses() {
        return busRepository.findAll().stream()
                .map(busMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BusDTO.busResponse> getBusesByStatus(StatusBus status) {
        return busRepository.findByStatusBus(status).stream()
                .map(busMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BusDTO.busResponse> getAvailableBuses(Integer minCapacity) {
        return busRepository.findAvailableBusesByCapacity(StatusBus.ACTIVE, minCapacity).stream()
                .map(busMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBus(Long id) {
        if (!busRepository.existsById(id)) {
            throw new IllegalArgumentException("Bus not found: " + id);
        }
        busRepository.deleteById(id);
    }

    @Override
    public boolean existsByPlate(String plate) {
        return busRepository.existsByPlate(plate);
    }

    @Override
    public BusDTO.busResponse changeBusStatus(Long id, StatusBus status) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bus not found: " + id));
        bus.setStatusBus(status);
        Bus updatedBus = busRepository.save(bus);
        return busMapper.toResponse(updatedBus);
    }

    @Override
    public Bus getObject(Long id) {
        return busRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bus not found with ID: " + id));
    }
}

