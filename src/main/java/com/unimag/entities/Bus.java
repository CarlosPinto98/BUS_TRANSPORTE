package com.unimag.entities;

import com.unimag.entities.Enums.Status_Bus;
import com.unimag.entities.Enums.Status_User;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter
@Table(name = "buses")

public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 10)
    private String plate;

    @Column(nullable = false)
    private Integer capacity;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> amenities = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status_Bus status_bus = Status_Bus.ACTIVE;

    @Builder.Default
    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();

}
