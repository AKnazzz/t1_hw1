package com.example.taskmanager.service;

import com.example.taskmanager.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskStatusProducerTest {

    private static final String TEST_TOPIC = "test-topic";

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private TaskStatusProducer taskStatusProducer;

    @BeforeEach
    void setUp() {
        taskStatusProducer = new TaskStatusProducer(kafkaTemplate);
        taskStatusProducer.setTopicName(TEST_TOPIC);
    }

    @Test
    void shouldSendTaskStatusUpdateSuccessfully() {
        // Arrange
        String expectedMessage = "1:COMPLETED";
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));

        when(kafkaTemplate.send(eq(TEST_TOPIC), eq(expectedMessage)))
                .thenReturn(future);

        // Act
        taskStatusProducer.sendTaskStatusUpdate(1L, TaskStatus.COMPLETED);

        // Assert
        verify(kafkaTemplate).send(TEST_TOPIC, expectedMessage);
    }

    @Test
    void shouldHandleKafkaSendFailure() {
        // Arrange
        String expectedMessage = "2:FAILED";
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka error"));

        when(kafkaTemplate.send(eq(TEST_TOPIC), eq(expectedMessage)))
                .thenReturn(future);

        // Act & Assert
        assertDoesNotThrow(() -> {
            taskStatusProducer.sendTaskStatusUpdate(2L, TaskStatus.FAILED);
        });
    }
}
