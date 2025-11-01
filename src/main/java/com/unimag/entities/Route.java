package com.unimag.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Length;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter

@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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
}
