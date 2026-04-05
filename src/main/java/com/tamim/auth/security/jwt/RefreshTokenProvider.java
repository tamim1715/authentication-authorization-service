package com.tamim.auth.security.jwt;

import com.tamim.auth.model.RefreshToken;
import com.tamim.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class RefreshTokenProvider {

    private final RefreshTokenRepository refreshTokenRepository;

    public String CreateRefreshToken(String userId, long accessTokenExpiration) {
        String rowToken = generateSecureToken();
        String hash = hashToken(rowToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .tokenHash(hash)
                .revoked(false)
                .expiresAt(Instant.now().plusSeconds(accessTokenExpiration))
                .build();

        refreshTokenRepository.save(refreshToken);

        return rowToken;
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[64];
        new SecureRandom().nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new RuntimeException("Token hashing failed");
        }
    }
}
