package com.example.taskmanager.service;

import com.example.taskmanager.model.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    @DisplayName("Отправка уведомления успешно")
    void shouldSendNotificationSuccessfully() {
        // Arrange
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        notificationService.sendStatusChangeNotification(1L, TaskStatus.COMPLETED, "test@example.com");

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Обработка ошибки при отправке почты")
    void shouldHandleMailException() {
        // Arrange
        doThrow(new MailException("Mail error") {
        }).when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        notificationService.sendStatusChangeNotification(1L, TaskStatus.COMPLETED, "test@example.com");

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        // Ошибки логируются, но не выбрасываются дальше
    }

    @Test
    @DisplayName("Проверка корректного содержимого письма")
    void shouldSendCorrectEmailContent() {
        // Arrange
        SimpleMailMessage capturedMessage = new SimpleMailMessage();
        doAnswer(invocation -> {
            SimpleMailMessage message = invocation.getArgument(0);
            capturedMessage.setTo(message.getTo());
            capturedMessage.setSubject(message.getSubject());
            capturedMessage.setText(message.getText());
            return null;
        }).when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        notificationService.sendStatusChangeNotification(2L, TaskStatus.PENDING, "recipient@example.com");

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        assert capturedMessage.getTo() != null;
        assertEquals("recipient@example.com", capturedMessage.getTo()[0]);
        assertEquals("Task Status Update", capturedMessage.getSubject());
        assertEquals("The status of task #2 has been changed to PENDING", capturedMessage.getText());
    }

    @Test
    @DisplayName("Не отправлять уведомление, если email пустой")
    void shouldNotSendNotificationWhenEmailIsEmpty() {
        // Arrange
        String emptyEmail = "";

        // Act
        notificationService.sendStatusChangeNotification(3L, TaskStatus.IN_PROGRESS, emptyEmail);

        // Assert
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Не отправлять уведомление, если email равен null")
    void shouldNotSendNotificationWhenEmailIsNull() {
        // Arrange
        String nullEmail = null;

        // Act
        notificationService.sendStatusChangeNotification(3L, TaskStatus.IN_PROGRESS, nullEmail);

        // Assert
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }
}
