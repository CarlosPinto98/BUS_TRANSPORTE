package com.unimag.repository;

import com.unimag.entities.Enums.StatusTrip;
import com.unimag.entities.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByRouteIdAndDate(Long routeId, LocalDate date);

    List<Trip> findByRouteIdAndDateAndStatusTrip(Long routeId, LocalDate date, StatusTrip status);

    List<Trip> findByDateAndStatusTrip(LocalDate date, StatusTrip status);

    @Query("SELECT t FROM Trip t JOIN FETCH t.route JOIN FETCH t.bus WHERE t.id = :id")
    Optional<Trip> findByIdWithDetails(Long id);

    @Query("SELECT t FROM Trip t WHERE t.date = :date AND t.statusTrip = :status " +
            "AND t.departureAt BETWEEN :startTime AND :endTime")
    List<Trip> findByDateAndTimeRange(
            @Param("date") LocalDate date,
            @Param("status") StatusTrip status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT t FROM Trip t WHERE t.bus.id = :busId AND t.date = :date " +
            "AND t.statusTrip NOT IN ('CANCELLED', 'ARRIVED')")
    List<Trip> findActiveTripsByBusAndDate(@Param("busId") Long busId, @Param("date") LocalDate date);

    List<Trip> findByStatusTripAndDepartureAtBefore(StatusTrip statusTrip, LocalDateTime dateTime);
}
