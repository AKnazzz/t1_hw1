package com.example.taskmanager.service;

import com.example.taskmanager.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
    @DisplayName("Успешная отправка сообщения в Kafka")
    void shouldSendTaskStatusUpdateSuccessfully() {
        // Arrange
        String expectedMessage = "1:COMPLETED";

        doAnswer(invocation -> {
            String topic = invocation.getArgument(0);
            String message = invocation.getArgument(1);

            assertEquals(TEST_TOPIC, topic);
            assertEquals(expectedMessage, message);
            return new CompletableFuture<>();
        }).when(kafkaTemplate).send(any(), any());

        // Act
        taskStatusProducer.sendTaskStatusUpdate(1L, TaskStatus.COMPLETED);

        // Verify
        verify(kafkaTemplate, times(1)).send(TEST_TOPIC, expectedMessage);
    }

    @Test
    @DisplayName("Отправка валидного сообщения в Kafka")
    void shouldSendValidMessage() {
        // Arrange
        String expectedMessage = "1:COMPLETED";
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));

        when(kafkaTemplate.send(eq(TEST_TOPIC), eq(expectedMessage)))
                .thenReturn(future);

        // Act
        assertDoesNotThrow(() -> taskStatusProducer.sendTaskStatusUpdate(1L, TaskStatus.COMPLETED));

        // Assert
        verify(kafkaTemplate).send(TEST_TOPIC, expectedMessage);
    }

    @Test
    @DisplayName("Не отправлять сообщение при недопустимом статусе задачи")
    void shouldNotSendMessageWithInvalidTaskStatus() {
        // Arrange
        Long taskId = 2L;
        TaskStatus invalidStatus = null;

        // Mock behavior for KafkaTemplate
        doReturn(new CompletableFuture<>()).when(kafkaTemplate).send(eq(TEST_TOPIC), eq(taskId + ":" + invalidStatus));

        // Act & Assert
        assertDoesNotThrow(() -> taskStatusProducer.sendTaskStatusUpdate(taskId, invalidStatus));

        // Verify no interactions as status is invalid
        verify(kafkaTemplate, never()).send(any(), any());
    }

    @Test
    @DisplayName("Обработка ошибки при отправке сообщения в Kafka")
    void shouldHandleKafkaSendFailure() {
        // Arrange
        String expectedMessage = "2:FAILED";
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka error"));

        when(kafkaTemplate.send(eq(TEST_TOPIC), eq(expectedMessage)))
                .thenReturn(future);

        // Act & Assert
        assertDoesNotThrow(() -> taskStatusProducer.sendTaskStatusUpdate(2L, TaskStatus.FAILED));

        // Verify
        verify(kafkaTemplate, times(1)).send(TEST_TOPIC, expectedMessage);
    }

    @Test
    @DisplayName("Отправка сообщения с пустым идентификатором задачи")
    void shouldHandleEmptyTaskId() {
        // Arrange
        String expectedMessage = ":COMPLETED";
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));

        when(kafkaTemplate.send(eq(TEST_TOPIC), eq(expectedMessage)))
                .thenReturn(future);

        // Act
        assertDoesNotThrow(() -> taskStatusProducer.sendTaskStatusUpdate(null, TaskStatus.COMPLETED));

        // Verify
        verify(kafkaTemplate, never()).send(any(), any());
    }

    @Test
    @DisplayName("Обработка недопустимого статуса задачи")
    void shouldHandleInvalidTaskStatus() {
        // Arrange
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka error")); // Simulate Kafka failure

        when(kafkaTemplate.send(any(), any())).thenReturn(future);

        // Act & Assert
        assertDoesNotThrow(() -> taskStatusProducer.sendTaskStatusUpdate(null, TaskStatus.COMPLETED));

        // Verify
        verify(kafkaTemplate, never()).send(any(), any());
    }

    @Test
    @DisplayName("Проверка корректности формата отправляемого сообщения")
    void shouldSendMessageWithCorrectFormat() {
        // Arrange
        Long taskId = 4L;
        TaskStatus status = TaskStatus.IN_PROGRESS;
        String expectedMessage = taskId + ":" + status;

        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));

        when(kafkaTemplate.send(eq(TEST_TOPIC), eq(expectedMessage)))
                .thenReturn(future);

        // Act
        taskStatusProducer.sendTaskStatusUpdate(taskId, status);

        // Assert
        verify(kafkaTemplate, times(1)).send(TEST_TOPIC, expectedMessage);
    }

    @Test
    @DisplayName("Не выбрасывать ошибку при любом исключении Kafka")
    void shouldNotThrowWhenKafkaFails() {
        // Arrange
        String message = "5:PENDING";
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka failure"));

        when(kafkaTemplate.send(eq(TEST_TOPIC), eq(message)))
                .thenReturn(future);

        // Act & Assert
        assertDoesNotThrow(() -> taskStatusProducer.sendTaskStatusUpdate(5L, TaskStatus.PENDING));
    }

    @Test
    @DisplayName("Не отправлять сообщение с недопустимым статусом задачи")
    void shouldNotSendMessageWithNullStatus() {
        // Arrange
        Long taskId = 2L;

        // Act & Assert
        assertDoesNotThrow(() -> taskStatusProducer.sendTaskStatusUpdate(taskId, null));

        // Verify
        verify(kafkaTemplate, never()).send(any(), any());
    }

    @Test
    @DisplayName("Не отправлять сообщение при отсутствии ID задачи")
    void shouldNotSendMessageWithNullTaskId() {
        // Arrange
        TaskStatus status = TaskStatus.COMPLETED;

        // Act & Assert
        assertDoesNotThrow(() -> taskStatusProducer.sendTaskStatusUpdate(null, status));

        // Verify
        verify(kafkaTemplate, never()).send(any(), any());
    }

}
