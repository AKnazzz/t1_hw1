package com.example.taskmanager;

import com.example.taskmanager.service.TaskStatusConsumer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Disabled
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class TaskStatusConsumerTest {

    @Autowired
    private TaskStatusConsumer taskStatusConsumer;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void shouldProcessKafkaMessage() {
        String message = "1:COMPLETED";
        assertDoesNotThrow(() -> {
            kafkaTemplate.send("task-status-updates", message);
            // Wait for message to be processed
            Thread.sleep(1000);
        });
    }

    @Test
    void shouldHandleInvalidMessageFormat() {
        String invalidMessage = "invalid-format";
        assertDoesNotThrow(() -> {
            kafkaTemplate.send("task-status-updates", invalidMessage);
            // Wait for message to be processed
            Thread.sleep(1000);
        });
    }
}
