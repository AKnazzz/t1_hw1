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

    @KafkaListener(topics = "${kafka.topic.name}")
    public void listenTaskStatusUpdates(String message) {
        try {
            String[] parts = message.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid message format");
            }

            Long taskId = Long.parseLong(parts[0]);
            TaskStatus status = TaskStatus.valueOf(parts[1]);
            String email = "user@example.com";
            notificationService.sendStatusChangeNotification(taskId, status, email);
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
        }
    }
}
