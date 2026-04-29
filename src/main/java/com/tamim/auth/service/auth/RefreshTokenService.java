package com.tamim.auth.service.auth;

import com.tamim.auth.enums.RecordStatus;
import com.tamim.auth.exception.AuthorizationException;
import com.tamim.auth.model.RefreshToken;
import com.tamim.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private static final long EXPIRATION = 60 * 60 * 24 * 7; // 7 days

    public String generateRefreshToken(String userId) {
        String rawToken = generateSecureToken();
        String hash = hashToken(rawToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .tokenHash(hash)
                .revoked(false)
                .expiresAt(Instant.now().plusSeconds(EXPIRATION))
                .build();

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    // VALIDATE + ROTATE (used in refresh)
    public RefreshToken validateAndRotate(String rawString) {

        String hash = hashToken(rawString);

        RefreshToken token = refreshTokenRepository
                .findByTokenHashAndStatus(hash, RecordStatus.ACTIVE)
                .orElseThrow(() ->
                        new AuthorizationException("Invalid refresh token"));

        // reuse detection
        if (token.isRevoked()) {
            revokeAll(token.getUserId());

            throw new AuthorizationException(
                    "Refresh token reuse detected. All sessions revoked."
            );
        }

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new AuthorizationException("Refresh token expired");
        }

        // rotation
        token.setRevoked(true);
        refreshTokenRepository.save(token);

        return token;
    }

    public void revokeToken(String rawToken) {
        String hash = hashToken(rawToken);

        RefreshToken token = refreshTokenRepository
                .findByTokenHashAndStatus(hash, RecordStatus.ACTIVE)
                .orElseThrow(() -> new AuthorizationException("Invalid refresh token"));

        if (!token.isRevoked()) {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        }
    }

    public void revokeAll(String userId) {
        List<RefreshToken> tokens = refreshTokenRepository
                .findByUserIdAndRevokedFalseAndStatus(userId, RecordStatus.ACTIVE);

        tokens.forEach(t -> t.setRevoked(true));

        refreshTokenRepository.saveAll(tokens);
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
