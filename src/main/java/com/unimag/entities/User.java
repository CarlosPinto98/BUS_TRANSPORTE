package com.unimag.entities;

import com.unimag.entities.Enum.Role;
import com.unimag.entities.Enum.Status;
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

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true,name ="phone")
    private String phone;

    @Column(nullable = false,name ="passwordHash")
    private String passwordHash;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name ="roles")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name ="status")
    private Status status;





}
