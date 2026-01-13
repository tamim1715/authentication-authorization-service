package com.tamim.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.util.Optional;

@Configuration
@EnableMongoAuditing
public class MongoAuditingConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("system"); // replace with logged-in user
    }
}
