package com.unimag.service;

import com.unimag.DTO.ParcelDTO;
import com.unimag.entities.Parcel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParcelService {

    ParcelDTO.parcelResponse save(ParcelDTO.parcelCreateRequest parcelDTO);
    Page<ParcelDTO.parcelResponse> getAll(Pageable pageable);
    ParcelDTO.parcelResponse get(Long id);
    ParcelDTO.parcelResponse get(String tagCode);
    boolean delete(Long id);
    Parcel getObject(Long id);
    Parcel getObject(String tagCode);
    ParcelDTO.parcelResponse update(ParcelDTO.parcelUpdateRequest parcelDTO,  Long id);
}
