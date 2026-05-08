package com.tamim.auth.service.auth;

import com.tamim.auth.enums.RecordStatus;
import com.tamim.auth.exception.AuthorizationException;
import com.tamim.auth.model.PasswordResetToken;
import com.tamim.auth.model.User;
import com.tamim.auth.repository.PasswordResetTokenRepository;
import com.tamim.auth.repository.UserRepository;
import com.tamim.auth.util.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private static final long EXPIRATION = 60 * 15; // 15 minutes

    // forgot password request
    public void requestReset(String email) {

        Optional<User> userOpt = userRepository
                .findByEmailAndStatus(email, RecordStatus.ACTIVE);

        // Prevent user enumeration
        if (userOpt.isEmpty()) {
            return;
        }

        User user = userOpt.get();

        String rawToken = TokenUtils.generateSecureToken();
        String hashToken = TokenUtils.hashToken(rawToken);

        PasswordResetToken passwordResetToken =
                generatePasswordResetToken(hashToken, user);

        passwordResetTokenRepository.save(passwordResetToken);

        // TODO: send email
        //  link: https://frontend/reset-password?token=rawToken
    }

    public void resetPassword(String rawToken, String newPassword) {

        String hashToken = TokenUtils.hashToken(rawToken);

        PasswordResetToken token = passwordResetTokenRepository
                .findByTokenHashAndStatus(hashToken, RecordStatus.ACTIVE)
                .orElseThrow(() -> new AuthorizationException("Invalid or expired token"));

        if (token.isUsed()) {
            throw new AuthorizationException("Token already used");
        }

        if (token.getExpiredAt().isBefore(Instant.now())) {
            throw new AuthorizationException("Token expired");
        }

        User user = userRepository.findByIdAndStatus(token.getUserId(), RecordStatus.ACTIVE)
                .orElseThrow(() -> new AuthorizationException("User not found"));

        // update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Revoke all sessions
        refreshTokenService.revokeAll(user.getId());

        // mark token used
        token.setUsed(true);
        passwordResetTokenRepository.save(token);
    }

    private PasswordResetToken generatePasswordResetToken(
            String hashToken,
            User user
    ) {
        PasswordResetToken token = new PasswordResetToken();

        token.setUserId(user.getId());
        token.setTokenHash(hashToken);
        token.setUsed(false);
        token.setExpiredAt(Instant.now().plusSeconds(EXPIRATION));

        return token;
    }
}
