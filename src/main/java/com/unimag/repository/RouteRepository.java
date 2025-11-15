package com.unimag.repository;

import com.unimag.entities.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route,Long> {

    Optional<Route> findByCode(String code);
    List<Route> findByOriginAndDestination(String origin, String destination);
    List<Route> findByOriginContainingIgnoreCaseOrDestinationContainingIgnoreCase(
            String origin, String destination);

    @Query("SELECT r FROM Route r JOIN FETCH r.stops WHERE r.id = :id")
    Optional<Route> findByIdWithStops(Long id);

    boolean existsByCode(String code);

}
