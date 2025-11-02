package com.unimag.repository;

import com.unimag.entities.Enums.Status_Parcel;
import com.unimag.entities.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParcelRepository extends JpaRepository<Parcel,Long> {

    Optional<Parcel> findByCode(String code);

    List<Parcel> findByStatus(Status_Parcel status);

    List<Parcel> findByTripId(Long tripId);

    List<Parcel> findByTripIdAndStatus(Long tripId, Status_Parcel status);

    @Query("SELECT p FROM Parcel p WHERE p.senderPhone = :phone OR p.receiverPhone = :phone")
    List<Parcel> findByPhone(@Param("phone") String phone);

    @Query("SELECT p FROM Parcel p WHERE p.receiverPhone = :phone AND p.status_parcel = 'IN_TRANSIT'")
    List<Parcel> findPendingDeliveriesByReceiver(@Param("phone") String phone);

    @Query("SELECT p FROM Parcel p WHERE p.fromStop.id = :stopId OR p.toStop.id = :stopId")
    List<Parcel> findByStopId(@Param("stopId") Long stopId);

    @Query("SELECT COUNT(p) FROM Parcel p WHERE p.trip.id = :tripId AND p.status_parcel = 'IN_TRANSIT'")
    long countInTransitParcelsByTrip(@Param("tripId") Long tripId);

    boolean existsByCode(String code);
}
