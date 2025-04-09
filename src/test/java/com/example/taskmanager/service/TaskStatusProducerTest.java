package com.example.taskmanager.service;

import com.example.taskmanager.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskStatusProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private TaskStatusProducer taskStatusProducer;

    @Test
    void shouldSendTaskStatusUpdate() {
        String expectedMessage = "1:COMPLETED";

        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);

        taskStatusProducer.sendTaskStatusUpdate(1L, TaskStatus.COMPLETED);

        verify(kafkaTemplate).send("task-status-updates", expectedMessage);
    }
}
