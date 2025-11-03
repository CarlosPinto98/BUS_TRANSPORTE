package com.unimag.repository;

import com.unimag.entities.Enums.EntityType;
import com.unimag.entities.Enums.TypeIncident;
import com.unimag.entities.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident,Long> {

    List<Incident> findByEntityTypeAndEntityId(EntityType entityType, Long entityId);

    List<Incident> findByIncidentType(TypeIncident typeIncident);

    List<Incident> findByReportedById(Long reportedById);

    @Query("SELECT i FROM Incident i WHERE i.entityType = :entityType " +
            "AND i.entityId = :entityId ORDER BY i.createdAt DESC")
    List<Incident> findByEntityOrderByCreatedAtDesc(
            @Param("entityType") EntityType entityType,
            @Param("entityId") Long entityId);

    @Query("SELECT i FROM Incident i WHERE i.createdAt BETWEEN :start AND :end " +
            "ORDER BY i.createdAt DESC")
    List<Incident> findByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(i) FROM Incident i WHERE i.incidentType = :type " +
            "AND i.createdAt >= :since")
    long countByTypeAndCreatedAtAfter(
            @Param("type") TypeIncident typeIncident,
            @Param("since") LocalDateTime since);
}
