package com.example.taskmanager.service;

import com.example.taskmanager.model.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JavaMailSender mailSender;

    public void sendStatusChangeNotification(Long taskId, TaskStatus newStatus, String emailTo) {
        if (emailTo == null || emailTo.trim().isEmpty()) {
            log.warn("Notification not sent: email is null or empty for task {}", taskId);
            return;
        }

        log.info("Attempting to send notification for task {} to {}", taskId, emailTo);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailTo);
            message.setSubject("Task Status Update");
            message.setText(String.format(
                    "The status of task #%d has been changed to %s",
                    taskId, newStatus
            ));

            mailSender.send(message);
            log.info("Notification successfully sent for task {} to {}", taskId, emailTo);
        } catch (MailException e) {
            log.error("Failed to send notification for task {} to {}: {}", taskId, emailTo, e.getMessage(), e);
        }
    }
}

