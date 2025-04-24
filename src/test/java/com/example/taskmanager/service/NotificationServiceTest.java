package com.example.taskmanager.service;

import com.example.taskmanager.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void shouldSendNotification() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        notificationService.sendStatusChangeNotification(1L, TaskStatus.COMPLETED, "test@example.com");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldLogErrorWhenMailFails() {
        doThrow(new MailException("Mail error") {
        }).when(mailSender).send(any(SimpleMailMessage.class));

        notificationService.sendStatusChangeNotification(1L, TaskStatus.COMPLETED, "test@example.com");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
