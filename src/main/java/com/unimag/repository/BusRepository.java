package com.unimag.repository;

import com.unimag.entities.Bus;
import com.unimag.entities.Enums.StatusBus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus,Long> {

    Optional<Bus> findByPlate(String plate);

    @Query("SELECT b FROM Bus b WHERE b.statusBus = :status")
    List<Bus> findByStatusBus(StatusBus statusBus);

    @Query("SELECT b FROM Bus b JOIN FETCH b.seats WHERE b.id = :id")
    Optional<Bus> findByIdWithSeats(Long id);

    @Query("SELECT b FROM Bus b WHERE b.statusBus = :status AND b.capacity >= :minCapacity")
    List<Bus> findAvailableBusesByCapacity(StatusBus statusBus, Integer minCapacity);

    boolean existsByPlate(String plate);


}
