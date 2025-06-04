package com.leea.generator.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class GeneratorRunConfig {

    @Bean
    public CommandLineRunner startupMessage() {
        return args -> System.out.println("✅ Resource Usage Generator started — generating data every 2 seconds...");
    }
}
