package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.model.dto.TaskDto;
import com.example.taskmanager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    void createTask_ShouldReturnCreatedTask() throws Exception {
        TaskDto taskDto = new TaskDto("Test", "Desc", 1L, TaskStatus.PENDING);
        Task task = new Task(1L, "Test", "Desc", 1L, TaskStatus.PENDING);

        when(taskService.createTask(any(TaskDto.class))).thenReturn(task);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test"));
    }

    @Test
    void getTask_ShouldReturnTask() throws Exception {
        Task task = new Task(1L, "Test", "Desc", 1L, TaskStatus.PENDING);

        when(taskService.getTaskById(anyLong())).thenReturn(task);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test"));
    }

    @Test
    void updateTask_ShouldReturnUpdatedTask() throws Exception {
        TaskDto taskDto = new TaskDto("Updated", "New desc", 1L, TaskStatus.IN_PROGRESS);
        Task task = new Task(1L, "Updated", "New desc", 1L, TaskStatus.IN_PROGRESS);

        when(taskService.updateTask(anyLong(), any(TaskDto.class))).thenReturn(task);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void deleteTask_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllTasks_ShouldReturnTaskList() throws Exception {
        List<Task> tasks = List.of(
                new Task(1L, "Task 1", "Desc 1", 1L, TaskStatus.PENDING),
                new Task(2L, "Task 2", "Desc 2", 1L, TaskStatus.COMPLETED)
        );

        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].title").value("Task 2"));
    }
}