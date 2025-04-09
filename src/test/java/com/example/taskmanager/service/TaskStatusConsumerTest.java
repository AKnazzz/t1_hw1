package com.example.taskmanager.service;

import com.example.taskmanager.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskStatusConsumerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TaskStatusConsumer taskStatusConsumer;

    @Test
    void shouldProcessValidMessage() {
        String message = "1:COMPLETED";

        doNothing().when(notificationService).sendStatusChangeNotification(anyLong(), any(TaskStatus.class), anyString());

        taskStatusConsumer.listenTaskStatusUpdates(message);

        verify(notificationService).sendStatusChangeNotification(1L, TaskStatus.COMPLETED, "user@example.com");
    }

    @Test
    void shouldLogErrorForInvalidMessage() {
        String invalidMessage = "invalid_format";

        taskStatusConsumer.listenTaskStatusUpdates(invalidMessage);

        verifyNoInteractions(notificationService);
    }
}
