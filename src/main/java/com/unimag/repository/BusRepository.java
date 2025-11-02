package com.unimag.repository;

import com.unimag.entities.Bus;
import com.unimag.entities.Enums.Status_Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus,Long> {

    Optional<Bus> findByPlate(String plate);

    List<Bus> findByStatus(Status_Bus status);

    @Query("SELECT b FROM Bus b JOIN FETCH b.seats WHERE b.id = :id")
    Optional<Bus> findByIdWithSeats(Long id);

    @Query("SELECT b FROM Bus b WHERE b.status = :status AND b.capacity >= :minCapacity")
    List<Bus> findAvailableBusesByCapacity(Status_Bus status, Integer minCapacity);

    boolean existsByPlate(String plate);


}
