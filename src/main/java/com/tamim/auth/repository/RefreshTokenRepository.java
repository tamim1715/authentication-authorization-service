package com.tamim.auth.repository;

import com.tamim.auth.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository
        extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);
}
