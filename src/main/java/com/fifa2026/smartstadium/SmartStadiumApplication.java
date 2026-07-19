package com.fifa2026.smartstadium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main entry point for the FIFA 2026 Smart Stadiums Application.
 */
@SpringBootApplication
@EnableCaching
public class SmartStadiumApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartStadiumApplication.class, args);
    }
}
