package com.unimag.service;

import com.unimag.DTO.CityDTO;
import com.unimag.entities.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CityService {

    CityDTO.cityResponse save(CityDTO.cityCreateRequest request);
    CityDTO.cityResponse update(CityDTO.cityUpdateRequest cityUpdateRequest, Long id);
    CityDTO.cityResponse deleteById(Long id);
    CityDTO.cityResponse get(Long id);
    CityDTO.cityResponse get(String name);
    Page<CityDTO.cityResponse> getAll(PageRequest pageRequest);
    boolean delete(Long id);
    City getObject(Long id);
}
