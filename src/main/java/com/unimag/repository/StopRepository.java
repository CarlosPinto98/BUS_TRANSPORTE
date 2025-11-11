package com.unimag.repository;

import com.unimag.entities.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StopRepository extends JpaRepository<Stop,Long> {

    List<Stop> findByRouteIdOrderByOrderAsc(Long routeId);

    List<Stop> findByRouteId(Long routeId);

    List<Stop> findByNameContainingIgnoreCase(String name);
    Optional<Stop> findByNameIgnoreCase(String name);
}
