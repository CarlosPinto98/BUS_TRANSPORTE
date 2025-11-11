package com.unimag.service;

import com.unimag.DTO.CityDTO;
import com.unimag.entities.City;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.CityMapper;
import com.unimag.repository.CityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


@Service
@Transactional
@RequiredArgsConstructor

public class CityServiceImpl implements CityService {

    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private CityMapper cityMapper;

    @Override
    public CityDTO.cityResponse save(CityDTO.cityCreateRequest request) {
        var c = cityMapper.toEntity(request);
        return cityMapper.toResponse(cityRepository.save(c));
    }

    @Override
    public CityDTO.cityResponse update(CityDTO.cityUpdateRequest cityUpdateRequest, Long id) {
        var s = getObject(id);
        cityMapper.update(cityUpdateRequest, s);
        return cityMapper.toResponse(s);
    }

    @Override
    public CityDTO.cityResponse deleteById(Long id) {
        return null;
    }

    @Override
    public CityDTO.cityResponse get(Long id) {
        return cityMapper.toResponse(getObject(id));
    }

    @Override
    public CityDTO.cityResponse get(String name) {
        var s = cityRepository.findByName(name).orElseThrow(() -> new NotFoundException("City not found"));
        return cityMapper.toResponse(s);
    }

    @Override
    public Page<CityDTO.cityResponse> getAll(PageRequest pageRequest) {
        Page<City> cities = cityRepository.findAll(pageRequest);
        return cities.map(e ->  cityMapper.toResponse(e));
    }

    @Override
    public boolean delete(Long id) {
        var f = getObject(id);
        boolean check = false;
        if (f != null) {
            cityRepository.delete(f);
            check = true;
        }
        return check;
    }

    @Override
    public City getObject(Long id) {
        return cityRepository.findById(id).orElseThrow(() -> new NotFoundException("City not found"));
    }
}
