package com.tamim.auth.model;

import com.tamim.auth.enums.UserType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", indexes = {
        // MySQL handles the unique index for email via the @Column(unique=true)
        // but you can define a specific name for the index here if desired:
        @Index(name = "idx_user_email", columnList = "email")
})
public class User extends BaseEntity {

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String firstName;

    private String lastName;

    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    private boolean enabled;

    private boolean emailVerified;

    private Instant lastLoginAt;
}
