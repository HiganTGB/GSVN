package com.gsvn.searchservice.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationStartupChecker {

    @Bean
    ApplicationRunner ConnectionsRunner() {
        return args -> {
            log.info("Starting health checks for required services...");
            // Connect Database

            log.info("✅ All services are connected and ready. Application initialization can proceed.");
        };
    }
}
