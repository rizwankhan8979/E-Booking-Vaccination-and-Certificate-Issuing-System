package com.example.vaccineManagement.Entity;

import com.example.vaccineManagement.Enums.Role;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "auth_users")
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean emailVerified = false;

    @OneToOne(mappedBy = "authUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private User user;
}
