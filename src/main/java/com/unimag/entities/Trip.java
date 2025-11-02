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
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalDateTime departureAt;

    @Column(nullable = false)
    private LocalDateTime arrivalEta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routeID", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "busID", nullable = false)
    private Bus bus;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status_Trip status_trip = Status_Trip.SCHEDULED;
}
