package com.unimag.service;

import com.unimag.DTO.RouteDTO;
import com.unimag.DTO.SeatDTO;
import com.unimag.entities.Seat;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.SeatMapper;
import com.unimag.repository.SeatRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor

public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;
    private final BusService busService;

//    @Override
//    public RouteDTO.routeResponse create(RouteDTO.routeCreateRequest request) {
//        log.info("Creating new seat {} for bus: {}", request.number(), request.busId());
//
//        SeatResponse created = seatService.createSeat(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(created);
//    }

    @Override
    public RouteDTO.routeResponse create(SeatDTO.@Valid seatCreateRequest createRequest) {
        return null;
    }

    @Override
    public SeatDTO.seatResponse save(SeatDTO.seatCreateRequest seatDTO) {
        return null;
    }

    @Override
    public SeatDTO.seatResponse update(SeatDTO.seatUpdateRequest updateRequest, Long id) {
        var f = getObject(id);
        seatMapper.updateEntity(updateRequest, f);
        f.setBus(busService.getObject(updateRequest.busId()));
        if (updateRequest.number() != null && isSeatNumberFree(String.valueOf(updateRequest.number()), f.getBus().getId())) {
            f.setNumber(String.valueOf(updateRequest.number()));
        }
        return seatMapper.toResponse(f);
    }

    @Override
    public void delete(Long id) {
        seatRepository.deleteById(id);
    }

    @Override
    public SeatDTO.seatResponse get(Long id) {
        return seatMapper.toResponse(getObject(id));
    }

    @Override
    public Page<SeatDTO.seatResponse> getAll(Pageable pageable) {
        return seatRepository.findAll(pageable).map(seatMapper::toResponse);
    }

    @Override
    public Seat getSeatByNumberAndBusId(int number, Long busId) {
        return seatRepository.findByBusIdAndNumber( busId, String.valueOf(number)).orElseThrow(()->new NotFoundException("Seat not found"));
    }

    @Override
    public Seat getObject(Long id) {
        return seatRepository.findById(id).orElseThrow(() -> new NotFoundException("Seat not found"));
    }

    private Boolean isSeatNumberFree(String number, Long busId){
        var s = seatRepository.findByNumberAndBusId(number, busId);
        if (s.isEmpty()) {return true;}
        throw new IllegalArgumentException("numero de asiento ocupado");
    }
}
