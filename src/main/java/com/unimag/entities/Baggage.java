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
@Table(name = "baggages")
public class Baggage {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal fee;

    @Column(nullable = false, unique = true, length = 50)
    private String tagCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticketID", nullable = false)
    private Ticket ticket;
}
