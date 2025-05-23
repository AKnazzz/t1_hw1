package com.example.taskmanager;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    public KafkaTemplate kafkaTemplate() {
        return mock(KafkaTemplate.class);
    }

}