package com.tamim.auth.service;

import com.tamim.auth.exception.AuthorizationException;
import com.tamim.auth.model.RefreshToken;
import com.tamim.auth.repository.RefreshTokenRepository;
import com.tamim.auth.service.auth.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void create_refresh_token_success() {

        String userId = "user-123";
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);

        String token = refreshTokenService.generateRefreshToken(userId);

        assertNotNull(token);
        assertTrue(token.length() > 20);

        // Verify using the captor
        verify(refreshTokenRepository).save(captor.capture());

        assertEquals(userId, captor.getValue().getUserId());
        RefreshToken savedToken = captor.getValue();
        assertEquals(userId, savedToken.getUserId());
        assertNotNull(savedToken.getTokenHash());
        assertFalse(savedToken.isRevoked());
        assertTrue(savedToken.getExpiresAt().isAfter(Instant.now()));
    }

    @Test
    void validateAndRotate_success() {
        // Arrange
        String rawToken = "some-valid-token";
        String userId = "user-123";

        // Create a dummy token that is NOT revoked and NOT expired
        RefreshToken existingToken = RefreshToken.builder()
                .userId(userId)
                .revoked(false)
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        // Mock the repo to return our dummy token
        Mockito.when(refreshTokenRepository.findByTokenHashAndStatus(anyString(), any()))
                .thenReturn(Optional.of(existingToken));

        // Act
        RefreshToken result = refreshTokenService.validateAndRotate(rawToken);

        // Assert
        assertTrue(result.isRevoked(), "Token should be marked as revoked after rotation");
        verify(refreshTokenRepository).save(existingToken);
    }

    @Test
    void validateAndRotate_reuseDetected_throwsException() {
        // Arrange
        String rawToken = "reused-token";
        String userId = "user-123";

        // Create a dummy token that is revoked
        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .revoked(true)
                .build();

        // Mock the repo to return our dummy token
        Mockito.when(refreshTokenRepository.findByTokenHashAndStatus(anyString(), any()))
                .thenReturn(Optional.of(token));

        // Act and Assert
        assertThrows(AuthorizationException.class,
                () -> refreshTokenService.validateAndRotate(rawToken));
    }

    @Test
    void validateAndRotate_expiredToken_throwsException() {
        // Arrange
        String rawToken = "expired-token";

        // Create a dummy token which is expired
        RefreshToken token = RefreshToken.builder()
                .expiresAt(Instant.now().minusSeconds(10))
                .build();

        // Mock the repo to return our dummy token
        Mockito.when(refreshTokenRepository.findByTokenHashAndStatus(anyString(), any()))
                .thenReturn(Optional.of(token));

        // Act and Assert
        assertThrows(AuthorizationException.class,
                () -> refreshTokenService.validateAndRotate(rawToken));
    }
}
