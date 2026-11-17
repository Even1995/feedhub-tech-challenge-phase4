package com.feedhub.notification.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
public class SesConfig {

    @Bean
    public SesClient sesClient(Environment env) {
        String region = env.getProperty("AWS_REGION", "sa-east-1");
        return SesClient.builder().region(Region.of(region)).build();
    }
}
