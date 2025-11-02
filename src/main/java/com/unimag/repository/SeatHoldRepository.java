package com.unimag.repository;

import com.unimag.entities.Enums.Status_SeatHold;
import com.unimag.entities.SeatHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatHoldRepository extends JpaRepository<SeatHold,Long> {

    Optional<SeatHold> findByTripIdAndSeatNumberAndStatus(
            Long tripId, String seatNumber, Status_SeatHold status);

    List<SeatHold> findByTripIdAndStatus(Long tripId, Status_SeatHold status);

    List<SeatHold> findByUserIdAndStatus(Long userId, Status_SeatHold status);

    @Query("SELECT sh FROM SeatHold sh WHERE sh.status = 'HOLD' " +
            "AND sh.expiresAt < :currentTime")
    List<SeatHold> findExpiredHolds(@Param("currentTime") LocalDateTime currentTime);

    @Modifying
    @Query("UPDATE SeatHold sh SET sh.status = 'EXPIRED' " +
            "WHERE sh.status = 'HOLD' AND sh.expiresAt < :currentTime")
    int expireOldHolds(@Param("currentTime") LocalDateTime currentTime);

    boolean existsByTripIdAndSeatNumberAndStatus(
            Long tripId, String seatNumber, Status_SeatHold status);

    @Query("SELECT COUNT(sh) FROM SeatHold sh WHERE sh.trip.id = :tripId " +
            "AND sh.status = 'HOLD'")
    long countActiveHoldsByTrip(@Param("tripId") Long tripId);
}
