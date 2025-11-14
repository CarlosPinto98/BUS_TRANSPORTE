package com.unimag.entities;

import com.unimag.entities.Enums.StatusTrip;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

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
    private StatusTrip statusTrip = StatusTrip.SCHEDULED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fareRuleId")
    private FareRule fareRule;

    @OneToMany(mappedBy = "trip")
    private Set<SeatHold> seatHolds;

    public void addBus(Bus bus){
        if(this.bus != null){
            this.bus.getTrips().remove(this);
        }
        this.bus = bus;
        bus.getTrips().add(this);
    }

    public void addRoute(Route route){
        if(this.route != null){
            this.route.getTrips().remove(this);

        }
        this.route = route;
        route.getTrips().add(this);
    }

    public void addFareRule(FareRule fareRule){
        if(this.fareRule != null){
            this.fareRule.getTrips().remove(this);
        }
        this.fareRule = fareRule;
        fareRule.getTrips().add(this);
    }

}
