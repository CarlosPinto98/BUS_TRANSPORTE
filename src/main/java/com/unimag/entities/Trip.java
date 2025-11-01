package com.unimag.entities;

import com.unimag.entities.Enums.Status_Trip;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter

@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalDateTime departureAt;

    @Column(nullable = false)
    private LocalDateTime arrivalEta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status_Trip status_trip;

    @ManyToOne(optional = false)
    @JoinColumn(name = "routeID", nullable = false)
    private Route route;

    @ManyToOne(optional = false)
    @JoinColumn(name = "busID", nullable = false)
    private Bus bus;

}
