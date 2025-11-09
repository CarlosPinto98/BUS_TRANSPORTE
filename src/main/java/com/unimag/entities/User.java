package com.unimag.entities;

import com.unimag.entities.Enums.Role;
import com.unimag.entities.Enums.StatusUser;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Getter

@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false, length = 40)
    private String email;

    @Column(nullable = false, length = 10)
    private String phone;

    @Column(nullable = false,length = 200)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusUser statusUser =  StatusUser.ACTIVE;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt =  LocalDateTime.now();
}
