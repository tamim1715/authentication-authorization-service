package com.tamim.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder // Use SuperBuilder to include BaseEntity fields like 'id' and 'status'
@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_token_hash", columnList = "tokenHash"), // High priority for lookups
        @Index(name = "idx_user_id", columnList = "userId") // For clearing all user sessions
})
public class RefreshToken extends BaseEntity {

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, length = 512) // Hashes can be long
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    private boolean revoked;

    private String deviceId;

    private String ipAddress;
}
