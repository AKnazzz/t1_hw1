package com.example.taskmanager.service;

import com.example.taskmanager.model.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class TaskStatusProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private String topicName;

    public TaskStatusProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Value("${kafka.topic.task-status-updates}")
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public void sendTaskStatusUpdate(Long taskId, TaskStatus newStatus) {
        String message = String.format("%d:%s", taskId, newStatus);
        try {
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, message);
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send message to topic {}: {}", topicName, message, ex);
                } else {
                    log.info("Message sent successfully to topic {}: {}", topicName, message);
                }
            });
        } catch (Exception e) {
            log.error("Error sending message to Kafka", e);
            throw new RuntimeException("Failed to send Kafka message", e);
        }
    }
}
