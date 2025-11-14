package com.unimag.entities;

import com.unimag.entities.Enums.Type;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter

@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "busID", nullable = false)
    private Bus bus;

    @Column(nullable = false,length = 15)
    private String number;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Type type = Type.STANDARD;

    @OneToMany(mappedBy = "seat")
    private Set<SeatHold> seatHolds = new HashSet<>();

    public void setBus(Bus bus) {
        if (this.bus == bus){return;}

        Bus oldBus = this.bus;
        if (oldBus != null) {
            oldBus.getSeats().remove(this);
        }
        this.bus = bus;
        if (this.bus != null) {
            this.bus.getSeats().add(this);
        }
    }
}
