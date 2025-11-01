package com.unimag.entities;

import com.unimag.entities.Enums.EntityType;
import com.unimag.entities.Enums.Type_Incident;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter
@Table(name = "incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Long entityId;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type_Incident incidentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityType entityType;


}
