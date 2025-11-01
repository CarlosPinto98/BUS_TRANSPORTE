package com.unimag.entities;

import com.unimag.entities.Enums.PaymentMethod;
import com.unimag.entities.Enums.Status_Ticket;
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

    @Column(nullable = false)
    private String seatNumber;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(length = 512)
    private String qrCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status_Ticket status_ticket;

    @ManyToOne(optional = false)
    @JoinColumn(name = "passengerID", nullable = false)
    private User passenger;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fromStopID", nullable = false)
    private Stop fromStop;

    @ManyToOne(optional = false)
    @JoinColumn(name = "toStopId", nullable = false)
    private Stop toStop;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tripID", nullable = false)
    private Trip trip;

}
