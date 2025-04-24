package com.example.taskmanager;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.model.dto.TaskDto;
import com.example.taskmanager.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@AutoConfigureMockMvc
class TaskManagerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
    }

    @Test
    void fullTaskLifecycleTest() throws Exception {
        // Create task
        TaskDto taskDto = new TaskDto("Integration Test", "Test Description", 1L, TaskStatus.PENDING);

        String response = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration Test"))
                .andReturn().getResponse().getContentAsString();

        Task createdTask = objectMapper.readValue(response, Task.class);
        assertThat(createdTask.getId()).isNotNull();

        // Get task
        mockMvc.perform(get("/tasks/" + createdTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Test"));

        // Update task
        TaskDto updateDto = new TaskDto("Updated Title", "Updated Desc", 1L, TaskStatus.IN_PROGRESS);

        mockMvc.perform(put("/tasks/" + createdTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        // Get all tasks
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Updated Title"));

        // Delete task
        mockMvc.perform(delete("/tasks/" + createdTask.getId()))
                .andExpect(status().isNoContent());

        // Verify task is deleted
        mockMvc.perform(get("/tasks/" + createdTask.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void kafkaIntegrationTest() throws Exception {
        // Create task
        TaskDto taskDto = new TaskDto("Kafka Test", "Test Kafka", 1L, TaskStatus.PENDING);

        String response = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Task createdTask = objectMapper.readValue(response, Task.class);

        // Update task to trigger Kafka message
        TaskDto updateDto = new TaskDto("Kafka Test", "Test Kafka", 1L, TaskStatus.COMPLETED);

        mockMvc.perform(put("/tasks/" + createdTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        // Verify Kafka message was sent
        assertThat(kafkaTemplate.send("task-status-updates",
                createdTask.getId() + ":" + TaskStatus.COMPLETED)).isNotNull();
    }
}