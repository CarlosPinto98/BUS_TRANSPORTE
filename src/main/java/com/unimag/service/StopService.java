package com.unimag.service;

import com.unimag.DTO.StopDTO;
import com.unimag.entities.Stop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StopService {

    StopDTO.stopResponse save(StopDTO.stopCreateRequest stopDTO);
    StopDTO.stopResponse get(Long id);
    StopDTO.stopResponse get(String name);
    Page<StopDTO.stopResponse> getAll(Pageable pageable);
    Stop getObject(Long id);
    Stop getObject(String name);
    boolean delete(Long id);
    boolean delete(String name);
    StopDTO.stopResponse updateStop(StopDTO.stopUpdateRequest stopDTO, Long id);
}
