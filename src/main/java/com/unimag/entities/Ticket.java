package com.unimag.entities;

import com.unimag.entities.Enums.CancellationPolicy;
import com.unimag.entities.Enums.PaymentMethod;
import com.unimag.entities.Enums.StatusTicket;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private Long id;

    @Column(nullable = false,length = 10)
    private String seatNumber;

    @Column(nullable = false,precision = 10,scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(nullable = false, length = 100, unique = true)
    private String qrCode;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "refund_amount",precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancellation_policy", length = 50)
    private CancellationPolicy cancellationPolicy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private PaymentMethod paymentMethod;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 20)
    private StatusTicket statusTicket = StatusTicket.SOLD;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passengerID", nullable = false)
    private User passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fromStopID", nullable = false)
    private Stop fromStop; // ORIGEN

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toStopID", nullable = false)
    private Stop toStop; // DESTINO

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tripID", nullable = false)
    private Trip trip;

}
