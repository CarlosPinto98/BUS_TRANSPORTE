package com.unimag.service;

import com.unimag.DTO.TripDTO;
import com.unimag.entities.Enums.StatusTrip;
import com.unimag.entities.SeatHold;
import com.unimag.entities.Trip;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.TripMapper;
import com.unimag.repository.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor

public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;
    private final BusService busService;
    private final FareRuleService fareRuleService;
    private final RouteService routeService;

    @Override
    public TripDTO.tripResponse save(TripDTO.tripCreateRequest createRequest) {

        if (createRequest.departureAt().isAfter(createRequest.arrivalEta())) {
            throw new IllegalArgumentException("la salida no puede ser antes de la llegada");
        }
            var trip = tripMapper.toEntity(createRequest);
            var bus = busService.getObject(createRequest.busId());
            var route = routeService.getObject(createRequest.routeId());
            var fareRule = fareRuleService.getObject(createRequest.fareRuleId());

            trip.addRoute(route);
            trip.addBus(bus);
            trip.addFareRule(fareRule);
            var saved = tripRepository.save(trip);
            return tripMapper.toResponse(saved);

    }

    @Override
    public TripDTO.tripResponse update(TripDTO.tripUpdateRequest updateRequest, Long tripId) {
            var trip = getObject(tripId);

            if(trip.getStatusTrip().equals(StatusTrip.ARRIVED) && updateRequest.statusTrip().equals(StatusTrip.CANCELLED)){
                throw new IllegalArgumentException("No se puede cancelar un viaje finalizado");
            }
            if(trip.getStatusTrip().equals(StatusTrip.DEPARTED) && updateRequest.statusTrip().equals(StatusTrip.CANCELLED)){
                throw new IllegalArgumentException("No se puede cancelar un viaje que ya salio");
            }

            tripMapper.updateEntity(updateRequest, trip);

            if(updateRequest.routeId()!= null){
                trip.addRoute(routeService.getObject(updateRequest.routeId()));
            }

            if(updateRequest.busId()!= null){
                trip.addBus(busService.getObject(updateRequest.busId()));
            }

            if(updateRequest.fareRuleId() != null){
                trip.addFareRule(fareRuleService.getObject(updateRequest.fareRuleId()));
            }

            return tripMapper.toResponse(tripRepository.save(trip));
    }

    @Override
    public void delete(Long tripId) {
            tripRepository.deleteById(tripId);
    }

    @Override
    public TripDTO.tripResponse get(Long id) {
            return tripMapper.toResponse(getObject(id));
    }

    @Override
    public Page<TripDTO.tripResponse> getAll(Pageable pageable) {
            return  tripRepository.findAll(pageable).map(tripMapper::toResponse);
    }

    @Override
    public Trip getObject(Long id) {
        return tripRepository.findById(id).orElseThrow(() -> new NotFoundException("Trip not found"));
    }

//    @Override
//    public Page<TripDTO.tripResponse> getTripsByOriginAndDestination(Pageable pageable, String origin, String destination) {
//        return tripRepository.findAllTripsBetweenOriginAndDestination(origin, destination, pageable).map(tripMapper::toDTO);
//    }
//
//    @Override
//    public List<Integer> findSeatsHold(Long tripId) {
//        return tripRepository.findSeatHolds(tripId);
//    }
//
//    @Override
//    public List<SeatHold> findUnpaidSeatsHold(Long tripId) {
//        return tripRepository.findUnpaidSeatHolds(tripId);
//    }
}
