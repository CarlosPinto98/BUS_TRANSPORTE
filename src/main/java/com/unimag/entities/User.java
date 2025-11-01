package com.unimag.entities;

import com.unimag.entities.Enums.Role;
import com.unimag.entities.Enums.Status_User;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;

@Data
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
    private int id;

    @Column(name = "name")
    private String name;

    @Column(unique = true, nullable = false, length = 40)
    private String email;

    @Column(unique = true,name ="phone",length = 10)
    private String phone;

    @Column(nullable = false,length = 200)
    private String passwordHash;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name ="roles")
    private Role role;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name ="status")
    private Status_User status =   Status_User.ACTIVE;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt =  LocalDateTime.now();

}
