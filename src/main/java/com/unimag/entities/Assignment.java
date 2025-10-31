package com.unimag.entities;

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
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tripID", nullable = false)
    private Trip trip;

    @ManyToOne(optional = false)
    @JoinColumn(name = "driverID", nullable = false)
    private User driver;

    @ManyToOne(optional = false)
    @JoinColumn(name = "dispatcherID", nullable = false)
    private User dispatcher;

    @Column(nullable = false)
    private Boolean checklistOk;

    @Column(nullable = false)
    private LocalDateTime assignedAt;


}
