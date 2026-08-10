package com.gsvn.accountservice.config;
import com.gsvn.accountservice.service.RoleService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationStartupChecker {
     RedisTemplate<String, Object> redisTemplate;
    JdbcTemplate jdbcTemplate;
    @Bean
    ApplicationRunner ConnectionsRunner(RoleService roleService) {
        return args -> {
            log.info("Starting health checks for required services...");
            // Connect Redis
            try {
                String pong = redisTemplate.getConnectionFactory().getConnection().ping();
                if (!"PONG".equals(pong)) {
                    log.error("Redis server responded abnormally. Aborting startup.");
                    throw new IllegalStateException("Redis connection check failed.");
                }
                log.info("Redis connection verified successfully.");
            } catch (Exception e) {
                log.error("Failed to connect to Redis. Aborting startup.", e);
                throw new IllegalStateException("Redis connection error.");
            }
            // Connect Database
            try {
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                log.info("Database connection verified successfully.");
            } catch (Exception e) {
                log.error("Failed to connect to Database. Aborting startup.", e);
                throw new IllegalStateException("Database connection error.");
            }
            // Start Warn up Role and Permission
            roleService.warmUpCache();
            log.info(" All services are connected and ready. Application initialization can proceed.");
        };
    }
}
