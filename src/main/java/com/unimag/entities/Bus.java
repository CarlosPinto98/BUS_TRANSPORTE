package com.unimag.entities;

import com.unimag.entities.Enum.Status;
import jakarta.persistence.*;
import lombok.*;

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
    private int id;

    @Column(unique = true, nullable = false)
    private String plate;

    @Column(nullable = false)
    private Integer capacity;

    @Convert(converter = AmenitiesConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> amenities;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

}
