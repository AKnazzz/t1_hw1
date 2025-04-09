package com.example.taskmanager.service;

import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.model.dto.TaskDto;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskStatusProducer taskStatusProducer;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskDto taskDto;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setUserId(1L);
        task.setStatus(TaskStatus.PENDING);

        taskDto = new TaskDto();
        taskDto.setTitle("Updated Task");
        taskDto.setDescription("Updated Description");
        taskDto.setUserId(1L);
        taskDto.setStatus(TaskStatus.IN_PROGRESS);
    }

    @Test
    void createTask_ShouldReturnSavedTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task created = taskService.createTask(taskDto);

        assertNotNull(created);
        assertEquals("Test Task", created.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task found = taskService.getTaskById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void getTaskById_WhenTaskNotExists_ShouldThrowException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));
    }

    @Test
    void updateTask_WhenStatusChanged_ShouldSendKafkaMessage() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setStatus(TaskStatus.PENDING);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.updateTask(1L, taskDto);

        verify(taskStatusProducer).sendTaskStatusUpdate(1L, TaskStatus.PENDING);
    }

    @Test
    void updateTask_WhenStatusNotChanged_ShouldNotSendKafkaMessage() {
        taskDto.setStatus(TaskStatus.PENDING); // Тот же статус

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.updateTask(1L, taskDto);

        verify(taskStatusProducer, never()).sendTaskStatusUpdate(any(), any());
    }

    @Test
    void shouldCreateTask() {
        TaskDto taskDto = new TaskDto("Test Task", "Description", 1L, TaskStatus.PENDING);
        Task task = new Task(1L, "Test Task", "Description", 1L, TaskStatus.PENDING);

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task createdTask = taskService.createTask(taskDto);

        assertNotNull(createdTask);
        assertEquals("Test Task", createdTask.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldUpdateTaskStatus() {
        Task existingTask = new Task(1L, "Old Task", "Description", 1L, TaskStatus.PENDING);
        TaskDto updateDto = new TaskDto("New Task", "Updated Description", 1L, TaskStatus.COMPLETED);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

        Task updatedTask = taskService.updateTask(1L, updateDto);

        assertEquals(TaskStatus.COMPLETED, updatedTask.getStatus());
        verify(taskStatusProducer).sendTaskStatusUpdate(1L, TaskStatus.COMPLETED);
    }
}
