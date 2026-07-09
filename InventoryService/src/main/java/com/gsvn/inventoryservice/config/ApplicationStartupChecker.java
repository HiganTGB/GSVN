package com.gsvn.inventoryservice.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationStartupChecker {

    JdbcTemplate jdbcTemplate;
    @Bean
    ApplicationRunner ConnectionsRunner() {
        return args -> {
            log.info("Starting health checks for required services...");
            try {
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                log.info("Database connection verified successfully.");
            } catch (Exception e) {
                log.error("Failed to connect to Database. Aborting startup.", e);
                throw new IllegalStateException("Database connection error.");
            }
            log.info("✅ All services are connected and ready. Application initialization can proceed.");
        };
    }
}
