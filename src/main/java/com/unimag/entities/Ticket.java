package com.unimag.entities;

import com.unimag.entities.Enums.PaymentMethod;
import com.unimag.entities.Enums.Status_Ticket;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(nullable = false,length = 10)
    private String seatNumber;

    @Column(nullable = false,precision = 10,scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 100, unique = true)
    private String qrCode;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private Status_Ticket status_ticket = Status_Ticket.SOLD;

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
