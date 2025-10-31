package com.unimag.entities;

import com.unimag.entities.Enum.PaymentMethod;
import com.unimag.entities.Enum.Status;
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
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tripID", nullable = false)
    private Trip trip;

    @ManyToOne(optional = false)
    @JoinColumn(name = "passengerID", nullable = false)
    private User passenger;

    @Column(nullable = false)
    private String seatNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fromStopID", nullable = false)
    private Stop fromStop;

    @ManyToOne(optional = false)
    @JoinColumn(name = "toStopId", nullable = false)
    private Stop toStop;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(length = 512)
    private String qrCode;

}
