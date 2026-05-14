package com.tamim.auth.repository;

import com.tamim.auth.model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmailVerificationTokenRepository
        extends JpaRepository<EmailVerificationToken, String> {

    Optional<EmailVerificationToken> findByTokenHash(String token);

    List<EmailVerificationToken> findAllByUserIdAndUsedIsFalse(String userId);
}
