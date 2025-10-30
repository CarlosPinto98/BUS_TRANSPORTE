package com.unimag.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter

public class SeatHold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
}
