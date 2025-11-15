package com.unimag.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter
@Data
@Table(name = "routes")

public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false,length = 20)
    private String origin;

    @Column(nullable = false,length = 20)
    private String destination;

    @Column(nullable = false)
    private Integer distanceKm;

    @Column(nullable = false)
    private Integer durationMin;

    @Builder.Default
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("order ASC")
    private List<Stop> stops = new ArrayList<>();

    @OneToMany(mappedBy = "route")
    private List<Trip> trips = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "originId")
    private Stop originStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinationId")
    private Stop destinationStop;


    public void addOrigin(Stop originStop) {
        this.originStop = originStop;
        originStop.getOriginRoutes().add(this);
    }

    public void addDestination(Stop destinationStop) {
        this.destinationStop = destinationStop;
        destinationStop.getDestinationRoutes().add(this);
    }

}
