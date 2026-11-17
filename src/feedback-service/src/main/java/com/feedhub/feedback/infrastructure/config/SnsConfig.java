package com.feedhub.feedback.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class SnsConfig {

    @Bean
    public SnsClient snsClient(org.springframework.core.env.Environment env) {
        String region = env.getProperty("AWS_REGION", "sa-east-1");
        return SnsClient.builder().region(Region.of(region)).build();
    }
}
