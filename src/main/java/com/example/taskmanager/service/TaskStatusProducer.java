package com.example.taskmanager.service;

import com.example.taskmanager.model.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskStatusProducer {
    private static final String TOPIC = "task-status-updates";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendTaskStatusUpdate(Long taskId, TaskStatus newStatus) {
        log.info("Preparing to send task status update for Task ID: {}, Status: {}", taskId, newStatus);

        String message = String.format("%d:%s", taskId, newStatus);
        try {
            kafkaTemplate.send(TOPIC, message);
            log.info("Message successfully sent to topic '{}': {}", TOPIC, message);
        } catch (Exception e) {
            log.error("Failed to send message to topic '{}': {}", TOPIC, e.getMessage(), e);
        }
    }
}

