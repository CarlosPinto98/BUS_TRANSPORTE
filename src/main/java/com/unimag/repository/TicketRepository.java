package com.unimag.repository;

import com.unimag.entities.Enums.Status_Ticket;
import com.unimag.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket,Long> {

    Optional<Ticket> findByQrCode(String qrCode);

    List<Ticket> findByTripIdAndStatus(Long tripId, Status_Ticket status);

    List<Ticket> findByPassengerIdAndStatus(Long passengerId, Status_Ticket status);

    List<Ticket> findByPassengerId(Long passengerId);

    @Query("SELECT t FROM Ticket t JOIN FETCH t.trip tr JOIN FETCH t.passenger " +
            "WHERE t.id = :id")
    Optional<Ticket> findByIdWithDetails(Long id);

    @Query("SELECT t FROM Ticket t WHERE t.trip.id = :tripId " +
            "AND t.seatNumber = :seatNumber AND t.status_ticket = 'SOLD'")
    Optional<Ticket> findSoldTicketBySeat(@Param("tripId") Long tripId,
                                          @Param("seatNumber") String seatNumber);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.trip.id = :tripId " +
            "AND t.status_ticket = 'SOLD'")
    long countSoldTicketsByTrip(@Param("tripId") Long tripId);

    @Query("SELECT t FROM Ticket t WHERE t.trip.id = :tripId " +
            "AND t.status_ticket IN ('SOLD') " +
            "AND ((t.fromStop.order <= :stopOrder AND t.toStop.order > :stopOrder))")
    List<Ticket> findActiveTicketsForStop(@Param("tripId") Long tripId,
                                          @Param("stopOrder") Integer stopOrder);

    @Query("SELECT t FROM Ticket t WHERE t.trip.departureAt < :dateTime " +
            "AND t.status_ticket = 'SOLD'")
    List<Ticket> findTicketsForDepartedTrips(@Param("dateTime") LocalDateTime dateTime);
}
