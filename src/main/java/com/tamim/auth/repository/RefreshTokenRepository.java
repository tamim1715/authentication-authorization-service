package com.tamim.auth.repository;

import com.tamim.auth.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);
}
