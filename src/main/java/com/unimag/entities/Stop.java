package com.unimag.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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
}
