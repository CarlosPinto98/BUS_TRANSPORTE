package com.unimag.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter
@Table(name = "baggage")
public class Baggage {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ticketID", nullable = false)
    private Ticket ticket;

    @Column(nullable = false)
    private Double weightKg;

    @Column(nullable = false)
    private BigDecimal fee;

    @Column(unique = true, nullable = false)
    private String tagCode;

}
