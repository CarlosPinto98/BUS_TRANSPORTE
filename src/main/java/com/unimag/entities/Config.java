package com.unimag.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter
@Table(name = "config")
public class Config {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 100, name = "config_Key")
    private String key;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String value;

    // configuraciones comunes

    public static final String SEAT_HOLD_MINUTES = "seat.hold.minutes";
    public static final String OVERBOOKING_PERCENTAGE = "overbooking.percentage";
    public static final String NO_SHOW_FEE = "no.show.fee";
    public static final String BAGGAGE_FREE_WEIGHT_KG = "baggage.free.weight.kg";
    public static final String BAGGAGE_EXTRA_PRICE_PER_KG = "baggage.extra.price.per.kg";
    public static final String CANCELLATION_FULL_REFUND_HOURS = "cancellation.full.refund.hours";
    public static final String CANCELLATION_PARTIAL_REFUND_HOURS = "cancellation.partial.refund.hours";
    public static final String CANCELLATION_PARTIAL_REFUND_PERCENTAGE = "cancellation.partial.refund.percentage";

}
