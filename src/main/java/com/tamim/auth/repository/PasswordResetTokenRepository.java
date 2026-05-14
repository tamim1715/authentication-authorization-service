package com.tamim.auth.repository;

import com.tamim.auth.enums.RecordStatus;
import com.tamim.auth.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, String> {

    Optional<PasswordResetToken> findByTokenHashAndStatus(
            String tokenHash, RecordStatus status);

    List<PasswordResetToken> findAllByUserIdAndUsedIsFalse(String userId);
}
