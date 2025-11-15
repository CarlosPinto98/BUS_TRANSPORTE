package com.unimag.service;

import com.unimag.DTO.BusDTO.*;
import com.unimag.entities.Bus;
import com.unimag.entities.Enums.StatusBus;

import java.util.List;

public interface BusService {


    busResponse createBus(busCreateRequest createRequest);
    busResponse updateBus(Long id,busUpdateRequest updateRequest);
    busResponse getBusById(Long id);
    busResponse getBusWithSeats(Long id);
    busResponse getBusbyPlate(String plate);
    List<busResponse> getAllBuses();
    List<busResponse> getBusesByStatus(StatusBus status);
    List<busResponse> getAvailableBuses(Integer minCapacity);
    void deleteBus(Long id);
    boolean existsByPlate(String plate);
    busResponse changeBusStatus(Long id, StatusBus status);
    Bus getObject(Long id);

}
