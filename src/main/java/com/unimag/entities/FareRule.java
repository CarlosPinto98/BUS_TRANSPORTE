package com.unimag.entities;

import com.unimag.entities.Enums.DynamicPricing;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter
@Table(name = "fareRules")
public class FareRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "routeID", nullable = false)
    private Route route;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fromStopID", nullable = false)
    private Stop fromStop;

    @ManyToOne(optional = false)
    @JoinColumn(name = "toStopID", nullable = false)
    private Stop toStop;

    @Column(nullable = false)
    private BigDecimal basePrice;

    @Convert(converter = AmenitiesConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> discounts;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DynamicPricing dynamicPricing;


}
