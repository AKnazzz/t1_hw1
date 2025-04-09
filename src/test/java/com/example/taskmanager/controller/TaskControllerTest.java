package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskDto;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void createTask_ShouldReturnCreatedTask() throws Exception {
        TaskDto taskDto = new TaskDto("Test Task", "Test Description", 1L);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Test Task")))
                .andExpect(jsonPath("$.description", is("Test Description")));
    }

    @Test
    void getTask_ShouldReturnTask_WhenExists() throws Exception {
        Task task = taskRepository.save(new Task(null, "Test Task", "Test Desc", 1L));

        mockMvc.perform(get("/tasks/{id}", task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(task.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Test Task")));
    }

    @Test
    void getTask_ShouldReturnNotFound_WhenNotExists() throws Exception {
        long nonExistentId = 999L;

        mockMvc.perform(get("/tasks/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("TASK_NOT_FOUND")))
                .andExpect(jsonPath("$.message", containsString("Task not found with id: " + nonExistentId)));
    }

    @Test
    void updateTask_ShouldUpdateTask() throws Exception {
        Task task = taskRepository.save(new Task(null, "Old Title", "Old Desc", 1L));
        TaskDto updateDto = new TaskDto("New Title", "New Desc", 1L);

        mockMvc.perform(put("/tasks/{id}", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("New Title")));
    }

    @Test
    void deleteTask_ShouldDeleteTask() throws Exception {
        Task task = taskRepository.save(new Task(null, "To Delete", "Desc", 1L));

        mockMvc.perform(delete("/tasks/{id}", task.getId()))
                .andExpect(status().isNoContent());

        assertFalse(taskRepository.existsById(task.getId()));
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() throws Exception {
        taskRepository.saveAll(List.of(
                new Task(null, "Task 1", "Desc 1", 1L),
                new Task(null, "Task 2", "Desc 2", 1L)
        ));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Task 1")));
    }
}