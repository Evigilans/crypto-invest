package com.epam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CryptoInvestmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoInvestmentApplication.class, args);
    }
}
