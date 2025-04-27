package com.example.taskmanager.service;

import com.example.taskmanager.model.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskStatusConsumerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TaskStatusConsumer taskStatusConsumer;

    @Test
    @DisplayName("Обработка валидного сообщения: должно вызывать NotificationService")
    void shouldProcessValidMessage() {
        // Arrange
        String validMessage = "1:COMPLETED";
        doNothing().when(notificationService)
                .sendStatusChangeNotification(1L, TaskStatus.COMPLETED, "user@example.com");

        // Act
        taskStatusConsumer.listenTaskStatusUpdates(validMessage);

        // Assert
        verify(notificationService, times(1))
                .sendStatusChangeNotification(1L, TaskStatus.COMPLETED, "user@example.com");
    }

    @Test
    @DisplayName("Не вызывать NotificationService при неверном формате сообщения")
    void shouldNotCallServiceForInvalidFormat() {
        // Arrange
        String invalidMessage = "invalid-format";

        // Act
        taskStatusConsumer.listenTaskStatusUpdates(invalidMessage);

        // Assert
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Обработка сообщения с пустым ID задачи: должно логировать ошибку")
    void shouldNotProcessMessageWithEmptyTaskId() {
        // Arrange
        String invalidMessage = ":COMPLETED";

        // Act
        taskStatusConsumer.listenTaskStatusUpdates(invalidMessage);

        // Assert
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Обработка сообщения с недопустимым статусом: должно логировать ошибку")
    void shouldLogErrorForInvalidTaskStatus() {
        // Arrange
        String invalidMessage = "1:INVALID_STATUS";

        // Act
        taskStatusConsumer.listenTaskStatusUpdates(invalidMessage);

        // Assert
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Не обрабатывать сообщение с отсутствующим разделителем")
    void shouldNotProcessMessageWithoutDelimiter() {
        // Arrange
        String invalidMessage = "1COMPLETED";

        // Act
        taskStatusConsumer.listenTaskStatusUpdates(invalidMessage);

        // Assert
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Обработка сообщения: исключение при парсинге ID")
    void shouldLogErrorWhenTaskIdIsNotParsable() {
        // Arrange
        String invalidMessage = "not-a-number:COMPLETED";

        // Act
        taskStatusConsumer.listenTaskStatusUpdates(invalidMessage);

        // Assert
        verifyNoInteractions(notificationService);
    }
}
