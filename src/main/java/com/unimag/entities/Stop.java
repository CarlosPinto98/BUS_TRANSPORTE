package com.unimag.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "stops")
public class Stop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 50)
    private String name;

    @Column(name = "stopOrder", nullable = false)
    private Integer order;

    @Column(precision = 10, scale = 5)
    private BigDecimal lat;

    @Column(precision = 10, scale = 5)
    private BigDecimal lng;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "routeID", nullable = false)
    private Route route;

    @OneToMany(mappedBy = "origin")
    private Set<Route> originRoutes;

    @OneToMany(mappedBy = "destination")
    private Set<Route> destinationRoutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cityId")
    private City city;

    public void addCity(City city) {
        this.city = city;
        city.getStops().add(this);
    }

    public void removeCity(City city) {
        this.city = null;
        city.getStops().remove(this);
    }

    @OneToMany(mappedBy = "origin")
    private Set<Route> originRoutes;

    @OneToMany(mappedBy = "destination")
    private Set<Route> destinationRoutes;

}
