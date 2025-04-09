package com.example.taskmanager.service;

import com.example.taskmanager.model.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskStatusConsumer {
    private final NotificationService notificationService;

    @KafkaListener(topics = "${kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenTaskStatusUpdates(String message) {
        log.info("Received message: {}", message);

        try {
            String[] parts = message.split(":");
            if (parts.length != 2) {
                log.warn("Invalid message format: {}", message);
                throw new IllegalArgumentException("Invalid message format");
            }

            Long taskId = Long.parseLong(parts[0]);
            TaskStatus newStatus = TaskStatus.valueOf(parts[1]);

            log.info("Parsed Task ID: {}, New Status: {}", taskId, newStatus);

            String userEmail = "user@example.com";
            log.info("Sending notification to email: {}", userEmail);

            notificationService.sendStatusChangeNotification(taskId, newStatus, userEmail);
            log.info("Notification sent successfully for Task ID: {}", taskId);
        } catch (IllegalArgumentException e) {
            log.error("Invalid message format '{}': {}", message, e.getMessage());
        } catch (Exception e) {
            log.error("Error processing message '{}': {}", message, e.getMessage(), e);
        }
    }
}
