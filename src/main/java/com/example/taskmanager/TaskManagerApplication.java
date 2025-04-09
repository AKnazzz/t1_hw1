package com.example.taskmanager;

import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.service.TaskStatusProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class TaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }

    @Bean
    public CommandLineRunner testKafka(TaskStatusProducer producer) {
        return args -> {
            log.info("Sending test messages to Kafka...");
            producer.sendTaskStatusUpdate(1L, TaskStatus.CREATED);
            producer.sendTaskStatusUpdate(2L, TaskStatus.IN_PROGRESS);
            producer.sendTaskStatusUpdate(3L, TaskStatus.COMPLETED);
            log.info("Test messages sent successfully");
        };
    }
}
