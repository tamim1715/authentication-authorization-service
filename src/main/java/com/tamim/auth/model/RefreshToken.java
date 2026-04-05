package com.tamim.auth.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("refresh_tokens")
public class RefreshToken extends BaseEntity {

    private String userId;

    private String tokenHash;

    private Instant expiresAt;

    private boolean revoked;

    private String deviceId;

    private String ipAddress;
}
