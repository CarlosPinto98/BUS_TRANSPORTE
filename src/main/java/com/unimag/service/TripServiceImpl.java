package com.unimag.service;

import com.unimag.DTO.TripDTO;
import com.unimag.entities.Bus;
import com.unimag.entities.Enums.StatusTrip;
import com.unimag.entities.Route;
import com.unimag.entities.Trip;
import com.unimag.exception.NotFoundException;
import com.unimag.mappers.TripMapper;
import com.unimag.repository.BusRepository;
import com.unimag.repository.RouteRepository;
import com.unimag.repository.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.unimag.entities.Enums.StatusTrip.BOARDING;
import static com.unimag.entities.Enums.StatusTrip.SCHEDULED;

@Service
@Transactional
@RequiredArgsConstructor

public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;
    private final BusRepository busRepository;
    private final TripMapper tripMapper;

    @Override
    public TripDTO.tripResponse createTrip(TripDTO.tripCreateRequest request) {
        Route route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new IllegalArgumentException("Route not found: " + request.routeId()));

        if (request.busId() != null) {
            Bus bus = busRepository.findById(request.busId())
                    .orElseThrow(() -> new IllegalArgumentException("Bus not found: " + request.busId()));
            validateTripSchedule(request.busId(), request.date(), request.departureAt());
        }

        if (request.arrivalEta().isBefore(request.departureAt())) {
            throw new IllegalArgumentException("Arrival time must be after departure time");
        }

        Trip trip = tripMapper.toEntity(request);
        trip.setRoute(route);
        if (request.busId() != null) {
            Bus bus = new Bus();
            bus.setId(request.busId());
            trip.setBus(bus);
        }

        Trip savedTrip = tripRepository.save(trip);
        return tripMapper.toResponse(savedTrip);
    }

    @Override
    public TripDTO.tripResponse updateTrip(Long id, TripDTO.tripUpdateRequest request) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + id));

        if (request.busId() != null && !request.busId().equals(trip.getBus().getId())) {
            busRepository.findById(request.busId())
                    .orElseThrow(() -> new IllegalArgumentException("Bus not found: " + request.busId()));
            validateTripSchedule(request.busId(), trip.getDate(), request.departureAt());
        }

        if (request.arrivalEta().isBefore(request.departureAt())) {
            throw new IllegalArgumentException("Arrival time must be after departure time");
        }

        tripMapper.updateEntity(request, trip);
        Trip updatedTrip = tripRepository.save(trip);
        return tripMapper.toResponse(updatedTrip);
    }

    @Override
    public TripDTO.tripResponse getTripById(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + id));
        return tripMapper.toResponse(trip);
    }

    @Override
    public TripDTO.tripResponse getTripWithDetails(Long id) {
        Trip trip = tripRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + id));
        return tripMapper.toResponse(trip);
    }

    @Override
    public List<TripDTO.tripResponse> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(tripMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TripDTO.tripResponse> getTripsByRouteAndDate(Long routeId, LocalDate date) {
        if(!routeRepository.existsById(routeId)) {
            throw new IllegalArgumentException("Route not found: " + routeId);
        }
        return tripRepository.findByRouteIdAndDate(routeId, date).stream()
                .map(tripMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TripDTO.tripResponse> getTripsByRouteAndDateAndStatus(Long routeId, LocalDate date, StatusTrip status) {
        if(!routeRepository.existsById(routeId)) {
            throw new IllegalArgumentException("Route not found: " + routeId);
        }
        return tripRepository.findByRouteIdAndDateAndStatusTrip(routeId, date, status).stream()
                .map(tripMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TripDTO.tripResponse> searchTrips(Long routeId, LocalDate date, StatusTrip status) {
        if (routeId != null && date != null && status != null) {
            return getTripsByRouteAndDateAndStatus(routeId, date, status);
        } else if (routeId != null && date != null) {
            return getTripsByRouteAndDate(routeId, date);
        } else if (date != null && status != null) {
            return getTripsByDateAndStatus(date, status);
        }
        return getAllTrips();
    }

    @Override
    public List<TripDTO.tripResponse> getTripsByDateAndStatus(LocalDate date, StatusTrip status) {
        return tripRepository.findByDateAndStatusTrip(date, status).stream()
                .map(tripMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TripDTO.tripResponse> getActiveTripsByBus(Long busId, LocalDate date) {
        if (!busRepository.existsById(busId)) {
            throw new IllegalArgumentException("Bus not found: " + busId);
        }
        return tripRepository.findActiveTripsByBusAndDate(busId, date).stream()
                .map(tripMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTrip(Long id) {
        if (!tripRepository.existsById(id)) {
            throw new IllegalArgumentException("Trip not found: " + id);
        }
        tripRepository.deleteById(id);
    }

    @Override
    public TripDTO.tripResponse changeTripStatus(Long id, StatusTrip status) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + id));

        validateTripStatusTransition(trip.getStatusTrip(), status);

        trip.setStatusTrip(status);
        Trip updatedTrip = tripRepository.save(trip);
        return tripMapper.toResponse(updatedTrip);
    }

    @Override
    public void validateTripSchedule(Long busId, LocalDate date, LocalDateTime departureAt) {
        List<Trip> activeTrips = tripRepository.findActiveTripsByBusAndDate(busId, date);

        for (Trip existingTrip : activeTrips) {
            if (departureAt.isAfter(existingTrip.getDepartureAt().minusHours(1)) &&
                    departureAt.isBefore(existingTrip.getArrivalEta().plusHours(1))) {
                throw new IllegalArgumentException(
                        "Bus is already scheduled for another trip at this time");
            }
        }
    }

    @Override
    public Trip getObject(Long id) {
        return tripRepository.findById(id).orElseThrow(() -> new NotFoundException("Trip not found"));
    }

    private void validateTripStatusTransition(StatusTrip currentStatus, StatusTrip newStatus) {
        if (currentStatus == StatusTrip.CANCELLED || currentStatus == StatusTrip.ARRIVED) {
            throw new IllegalArgumentException("Cannot change status from " + currentStatus);
        }

        switch (currentStatus) {
            case SCHEDULED:
                if (newStatus != StatusTrip.BOARDING && newStatus != StatusTrip.CANCELLED) {
                    throw new IllegalArgumentException("Invalid status transition from SCHEDULED to " + newStatus);
                }
                break;
            case BOARDING:
                if (newStatus != StatusTrip.DEPARTED && newStatus != StatusTrip.CANCELLED) {
                    throw new IllegalArgumentException("Invalid status transition from BOARDING to " + newStatus);
                }
                break;
            case DEPARTED:
                if (newStatus != StatusTrip.ARRIVED) {
                    throw new IllegalArgumentException("Invalid status transition from DEPARTED to " + newStatus);
                }
                break;
        }
    }
}