package com.unimag.entities;
import com.unimag.entities.Enums.StatusSeatHold;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter
@Table(name = "seatHolds")
public class SeatHold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "tripID", nullable = false)
    private Trip trip;

    @Column(nullable = false, length = 10)
    private String seatNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seatId")
    private Seat seat;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt =  LocalDateTime.now();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusSeatHold statusSeatHold = StatusSeatHold.HOLD;

    public void setSeat(Seat seat) {
        if (this.seat == seat){return;}
        Seat oldSeat = this.seat;
        if (oldSeat != null){
            oldSeat.getSeatHolds().remove(this);
        }
        this.seat = seat;
        if (this.seat != null){
            this.seat.getSeatHolds().add(this);
        }
    }

    public void setUser(User user) {
        if (this.user == user){return;}
        User oldUser = this.user;
        if (oldUser != null){
            oldUser.getSeatHolds().remove(this);
        }
        this.user = user;
        if (this.user != null){
            this.user.getSeatHolds().add(this);
        }
    }

    public void setTrip(Trip trip) {
        if (this.trip == trip){return;}
        Trip oldTrip = this.trip;
        if (oldTrip != null){
            oldTrip.getSeatHolds().remove(this);
        }
        this.trip = trip;
        if (this.trip != null){
            this.trip.getSeatHolds().add(this);
        }
    }
}
