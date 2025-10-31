package com.unimag.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
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
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "routeID", nullable = false)
    private Route route;

    @Column(nullable = false)
    private String name;

    @Column(name = "stopOrder", nullable = false)
    private Integer order;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lng;
}
