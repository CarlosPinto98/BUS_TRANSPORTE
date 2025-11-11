package com.unimag.service;

import com.unimag.DTO.BusDTO;
import com.unimag.entities.Bus;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.BusMapper;
import com.unimag.repository.BusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor

public class BusServiceImpl implements BusService {

    @Autowired
    private final BusRepository busRepository;

    @Autowired
    private final BusMapper busMapper;

    @Override
    public BusDTO.busResponse save(BusDTO.busCreateRequest request) {
        return null;
    }

    @Override
    public BusDTO.busResponse get(Long id) {
        return null;
    }

    @Override
    public Page<BusDTO.busResponse> getAll(Pageable pageable) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public BusDTO.busResponse update(BusDTO.busUpdateRequest request, Long busId) {
        return null;
    }

    @Override
    public Bus getObject(Long id) {
        return busRepository.findById(id).orElseThrow(() -> new NotFoundException("Bus not found"));
    }
}

