package com.tamim.auth.service.auth;

import com.tamim.auth.constant.MessageConstants;
import com.tamim.auth.enums.RecordStatus;
import com.tamim.auth.exception.AuthorizationException;
import com.tamim.auth.model.EmailVerificationToken;
import com.tamim.auth.model.User;
import com.tamim.auth.repository.EmailVerificationTokenRepository;
import com.tamim.auth.repository.UserRepository;
import com.tamim.auth.service.email.EmailService;
import com.tamim.auth.util.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    private static final long EXPIRATION = 60 * 60 * 24;

    public void sendVerificationEmail(User user) {

        invalidateOldTokens(user.getId());

        String rawToken = TokenUtils.generateSecureToken();
        String hash = TokenUtils.hashToken(rawToken);

        EmailVerificationToken token =
                new EmailVerificationToken();

        token.setUserId(user.getId());
        token.setTokenHash(hash);
        token.setExpiresAt(
                Instant.now().plusSeconds(EXPIRATION)
        );
        token.setUsed(false);

        log.info("verification token: {}", rawToken);

        emailVerificationTokenRepository.save(token);

        emailService.sendVerificationEmail(
                user.getEmail(),
                rawToken
        );
    }

    public void verify(String rawToken) {

        String hash = TokenUtils.hashToken(rawToken);

        EmailVerificationToken token = emailVerificationTokenRepository
                .findByTokenHash(hash).orElseThrow(() ->
                        new AuthorizationException("Invalid verification token"));

        if (token.isUsed()) {
            throw new AuthorizationException("Verification token already used");
        }

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new AuthorizationException("Verification token expired");
        }

        User user = userRepository.findByIdAndStatus(token.getUserId(), RecordStatus.ACTIVE)
                .orElseThrow(() -> new AuthorizationException(MessageConstants.USER_NOT_FOUND));

        user.setEmailVerified(true);
        user.setEnabled(true);

        userRepository.save(user);

        token.setUsed(true);

        emailVerificationTokenRepository.save(token);
    }

    public void resendVerificationEmail(String email) {

        Optional<User> optionalUser = userRepository
                .findByEmailAndStatus(email, RecordStatus.ACTIVE);

        // anti-enumeration
        if (optionalUser.isEmpty()) {
            return;
        }

        User user = optionalUser.get();

        if (user.isEmailVerified()) {
            return;
        }

        sendVerificationEmail(user);
    }

    private void invalidateOldTokens(String userId) {

        List<EmailVerificationToken> tokens =
                emailVerificationTokenRepository.findAllByUserIdAndUsedIsFalse(userId);

        tokens.forEach(t -> t.setUsed(true));

        emailVerificationTokenRepository.saveAll(tokens);
    }
}
