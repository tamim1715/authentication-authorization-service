package com.tamim.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "email_verification_tokens")
@Getter
@Setter
public class EmailVerificationToken extends BaseEntity {

    @Column(nullable = false)
    private String userId;

    @Column(unique = true, nullable = false)
    private String tokenHash;

    private boolean used;

    private Instant expiresAt;
}
