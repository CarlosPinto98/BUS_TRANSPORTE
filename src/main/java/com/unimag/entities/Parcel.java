package com.unimag.entities;

import com.unimag.entities.Enums.Status_Parcel;
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
@Table(name = "parcels")
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String senderName;

    @Column(nullable = false)
    private String senderPhone;

    @Column(nullable = false)
    private String receiverName;

    @Column(nullable = false)
    private String receiverPhone;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fromStopID", nullable = false)
    private Stop fromStop;

    @ManyToOne(optional = false)
    @JoinColumn(name = "stopID", nullable = false)
    private Stop stop;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status_Parcel status_parcel;

    @Column(length = 512)
    private String proofPhotoUrl;

    @Column(length = 10)
    private String deliveryOtp;


}
