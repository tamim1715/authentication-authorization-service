package com.tamim.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        // This provides the value for @CreatedBy and @LastModifiedBy
        return () -> Optional.of("system");

        // When security complete
//        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
//                .map(auth -> auth.getName());

    }
}
