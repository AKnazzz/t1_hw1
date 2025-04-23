package com.example.apiloggingstarter.configuration;


import com.example.apiloggingstarter.aspect.ApiLoggingAspect;
import com.example.apiloggingstarter.properties.ApiLoggingProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ApiLoggingProperties.class)
@ConditionalOnProperty(prefix = "api.logging", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ApiLoggingAutoConfiguration {
    @Bean
    public ApiLoggingAspect apiLoggingAspect(ApiLoggingProperties properties) {
        return new ApiLoggingAspect(properties);
    }
}