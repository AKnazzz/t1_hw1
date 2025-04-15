package com.example.taskmanager.service;

import com.example.taskmanager.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class TaskStatusConsumerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TaskStatusConsumer taskStatusConsumer;

    @Test
    void shouldProcessValidMessage() {
        // Given
        String validMessage = "1:COMPLETED";
        doNothing().when(notificationService)
                .sendStatusChangeNotification(1L, TaskStatus.COMPLETED, "user@example.com");

        // When
        taskStatusConsumer.listenTaskStatusUpdates(validMessage);

        // Then
        verify(notificationService, times(1))
                .sendStatusChangeNotification(1L, TaskStatus.COMPLETED, "user@example.com");
    }

    @Test
    void shouldNotCallServiceForInvalidFormat() {
        // Given
        String invalidMessage = "invalid-format";

        // When
        taskStatusConsumer.listenTaskStatusUpdates(invalidMessage);

        // Then
        verifyNoInteractions(notificationService);
    }
}
