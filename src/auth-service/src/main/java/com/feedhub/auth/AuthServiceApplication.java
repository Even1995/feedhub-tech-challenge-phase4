package com.feedhub.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada Spring Boot do auth-service. O scanBasePackages inclui o
 * modulo compartilhado feedhub-common para que seus beans (seguranca, etc.) sejam carregados.
 */
@SpringBootApplication(scanBasePackages = "com.feedhub")
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
