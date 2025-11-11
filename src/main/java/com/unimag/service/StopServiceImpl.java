package com.unimag.service;

import com.unimag.DTO.StopDTO;
import com.unimag.entities.Stop;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.StopMapper;
import com.unimag.repository.StopRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor

public class StopServiceImpl implements StopService {

    @Autowired
    private final StopRepository stopRepository;

    @Autowired
    private final StopMapper stopMapper;

    @Autowired
    private final CityServiceImpl cityService;

    @Override
    public StopDTO.stopResponse save(StopDTO.stopCreateRequest createRequest) {
        var s = stopMapper.toEntity(stopDTO);
        s.addCity(cityService.getObject(StopDTO.cityId()));
        return stopMapper.toResponse(stopRepository.save(s));
    }

    @Override
    public StopDTO.stopResponse get(Long id) {
        return stopMapper.toResponse(getObject(id));
    }

    @Override
    public StopDTO.stopResponse get(String name) {
        return stopMapper.toResponse(getObject(name));
    }

    @Override
    public Page<StopDTO.stopResponse> getAll(Pageable pageable) {
        return stopRepository.findAll(pageable).map(stopMapper::toResponse);
    }

    @Override
    public Stop getObject(Long id) {
        var s = stopRepository.findById(id).orElseThrow(() -> new NotFoundException("Stop not found"));
        return s;
    }

    @Override
    public Stop getObject(String name) {
        var s = stopRepository.findByNameContainingIgnoreCase(name).orElseThrow(() -> new NotFoundException("Stop not found"));
        return s;
    }

    @Override
    public boolean delete(Long id) {
        var s = getObject(id);
        stopRepository.delete(s);
        return true;
    }

    @Override
    public boolean delete(String name) {
        var s = getObject(name);
        stopRepository.delete(s);
        return true;
    }

    @Override
    public StopDTO.stopResponse updateStop(StopDTO.stopUpdateRequest stopDTO, Long id) {
        var s = getObject(id);
        var city = s.getCity();
        stopMapper.updateStop(stopDTO, s);
        if (!city.getId().equals(stopDTO.cityId()) && stopDTO.cityId() != null) {
            s.removeCity(city);
            s.addCity(cityService.getObject(stopDTO.cityId()));
        }
        return stopMapper.toResponse(s);
    }
}
