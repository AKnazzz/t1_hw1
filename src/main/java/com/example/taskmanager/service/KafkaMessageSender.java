package com.example.taskmanager.service;

import com.example.taskmanager.model.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaMessageSender implements CommandLineRunner {

    private final TaskStatusProducer producer;

    public KafkaMessageSender(TaskStatusProducer producer) {
        this.producer = producer;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Sending test messages to Kafka...");
        producer.sendTaskStatusUpdate(1L, TaskStatus.CREATED);
        producer.sendTaskStatusUpdate(2L, TaskStatus.IN_PROGRESS);
        producer.sendTaskStatusUpdate(3L, TaskStatus.COMPLETED);
        log.info("Test messages sent successfully");
    }
}
