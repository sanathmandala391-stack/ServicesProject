package com.krishna.saibaba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SaibabaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaibabaApplication.class, args);
    }
}
