package com.tamim.auth.service.auth;

import com.tamim.auth.constant.MessageConstants;
import com.tamim.auth.enums.RecordStatus;
import com.tamim.auth.exception.AuthorizationException;
import com.tamim.auth.model.RefreshToken;
import com.tamim.auth.repository.RefreshTokenRepository;
import com.tamim.auth.util.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private static final long EXPIRATION = 60 * 60 * 24 * 7; // 7 days

    public String generateRefreshToken(String userId) {
        String rawToken = TokenUtils.generateSecureToken();
        String hash = TokenUtils.hashToken(rawToken);

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

        String hash = TokenUtils.hashToken(rawString);

        RefreshToken token = refreshTokenRepository
                .findByTokenHashAndStatus(hash, RecordStatus.ACTIVE)
                .orElseThrow(() ->
                        new AuthorizationException(
                                MessageConstants.INVALID_REFRESH_TOKEN)
                );

        // reuse detection
        if (token.isRevoked()) {
            revokeAll(token.getUserId());

            throw new AuthorizationException(
                    MessageConstants.REFRESH_TOKEN_REUSE_DETECTED
            );
        }

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new AuthorizationException(
                    MessageConstants.REFRESH_TOKEN_EXPIRED);
        }

        // rotation
        token.setRevoked(true);
        refreshTokenRepository.save(token);

        return token;
    }

    public void revokeToken(String rawToken) {
        String hash = TokenUtils.hashToken(rawToken);

        RefreshToken token = refreshTokenRepository
                .findByTokenHashAndStatus(hash, RecordStatus.ACTIVE)
                .orElseThrow(() -> new AuthorizationException(
                        MessageConstants.INVALID_REFRESH_TOKEN));

        if (token.isRevoked()) {
            revokeAll(token.getUserId());
        } else {
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
}
