package com.tamim.auth.model;

import com.tamim.auth.enums.RecordStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
@SuperBuilder // Required
@NoArgsConstructor // Required for JPA
@AllArgsConstructor // Required for SuperBuilder
@EntityListeners(AuditingEntityListener.class) // Required for @CreatedBy / @LastModifiedBy
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private String id;

    @Enumerated(EnumType.STRING) // Ensures "ACTIVE" is stored as a string in MySQL
    @Column(nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
