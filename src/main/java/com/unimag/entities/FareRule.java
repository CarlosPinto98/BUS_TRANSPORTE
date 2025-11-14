package com.unimag.entities;

import com.unimag.entities.Enums.DynamicPricing;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Builder.Default
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> discounts = new HashMap<>();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DynamicPricing dynamicPricing =  DynamicPricing.OFF;

    @ManyToOne(optional = false)
    @JoinColumn(name = "toStopID", nullable = false)
    private Stop toStop;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fromStopID", nullable = false)
    private Stop fromStop;

    @ManyToOne(optional = false)
    @JoinColumn(name = "routeID", nullable = false)
    private Route route;

    @OneToMany(mappedBy = "fareRule")
    private Set<Trip> trips;
}
