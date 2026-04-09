package com.tamim.auth.repository;

import com.tamim.auth.enums.RecordStatus;
import com.tamim.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmailAndStatus(String email, RecordStatus status);

    boolean existsByEmailAndStatus(String email, RecordStatus status);

    Optional<User> findByIdAndStatus(String id, RecordStatus status);

    List<User> findAllByStatus(RecordStatus status);
}
