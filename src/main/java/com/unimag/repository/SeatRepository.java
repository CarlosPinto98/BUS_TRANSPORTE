package com.unimag.repository;

import com.unimag.entities.Enums.Type;
import com.unimag.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByBusId(Long busId);

    List<Seat> findByBusIdOrderByNumberAsc(Long busId);

    Optional<Seat> findByBusIdAndNumber(Long busId, String number);

    List<Seat> findByBusIdAndType(Long busId, Type type);

    long countByBusId(Long busId);
}
