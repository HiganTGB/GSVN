package com.tgb.gsvnbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.tgb.gsvnbackend.repository.mongoRepository")
@EnableJpaRepositories(basePackages = "com.tgb.gsvnbackend.repository.jpaRepository")
@EnableElasticsearchRepositories(
        basePackages = "com.tgb.gsvnbackend.repository.elasticsearch"
)
public class GsvnBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(GsvnBackEndApplication.class, args);
    }

}
