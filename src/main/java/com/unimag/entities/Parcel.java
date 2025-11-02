package com.unimag.entities;

import com.unimag.entities.Enums.Status_Parcel;
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
@Table(name = "parcels")

public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = false, nullable = false,length = 20)
    private String code;

    @Column(nullable = false, length = 100)
    private String senderName;

    @Column(nullable = false, length = 20)
    private String senderPhone;

    @Column(nullable = false,length = 50)
    private String receiverName;

    @Column(nullable = false,length = 15)
    private String receiverPhone;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status_Parcel status_parcel =  Status_Parcel.CREATED;

    @Column(length = 200)
    private String proofPhotoUrl;

    @Column(length = 10)
    private String deliveryOtp;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deliveredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fromStopID", nullable = false)
    private Stop fromStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toStopId", nullable = false)
    private Stop toStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tripID")
    private Trip trip;

}
