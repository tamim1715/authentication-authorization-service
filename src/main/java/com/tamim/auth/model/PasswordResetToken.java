package com.tamim.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
public class PasswordResetToken extends BaseEntity {

    @Column(nullable = false)
    private String userId;

    @Column(unique = true, nullable = false)
    private String tokenHash;

    private Instant expiredAt;

    private boolean used;
}
