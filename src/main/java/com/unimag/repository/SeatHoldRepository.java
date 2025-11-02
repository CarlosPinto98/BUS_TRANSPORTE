package com.unimag.repository;

import com.unimag.entities.Enums.StatusSeatHold;
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

    Optional<SeatHold> findByTripIdAndSeatNumberAndStatusSeatHold(Long tripId, String seatNumber, StatusSeatHold status);

    List<SeatHold> findByTripIdAndStatusSeatHold(Long tripId, StatusSeatHold status);

    List<SeatHold> findByUserIdAndStatusSeatHold(Long userId, StatusSeatHold status);

    @Query("SELECT sh FROM SeatHold sh WHERE sh.statusSeatHold= 'HOLD' " +
            "AND sh.expiresAt < :currentTime")
    List<SeatHold> findExpiredHolds(@Param("currentTime") LocalDateTime currentTime);

    @Modifying
    @Query("UPDATE SeatHold sh SET sh.statusSeatHold = 'EXPIRED' " +
            "WHERE sh.statusSeatHold = 'HOLD' AND sh.expiresAt < :currentTime")
    int expireOldHolds(@Param("currentTime") LocalDateTime currentTime);

    boolean existsByTripIdAndSeatNumberAndStatusSeatHold(Long tripId, String seatNumber, StatusSeatHold statusSeatHold);


    @Query("SELECT COUNT(sh) FROM SeatHold sh WHERE sh.trip.id = :tripId " +
            "AND sh.statusSeatHold = 'HOLD'")
    long countActiveHoldsByTrip(@Param("tripId") Long tripId);
}
