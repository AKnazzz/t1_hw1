package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskDto;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_ShouldReturnSavedTask() {
        TaskDto taskDto = new TaskDto("Test Task", "Test Description", 1L);
        Task savedTask = new Task(1L, "Test Task", "Test Description", 1L);

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task result = taskService.createTask(taskDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Task", result.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void getTaskById_ShouldReturnTask_WhenExists() {
        Task task = new Task(1L, "Test Task", "Test Description", 1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getTaskById_ShouldThrowException_WhenNotExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));
    }

    @Test
    void updateTask_ShouldUpdateExistingTask() {
        Task existingTask = new Task(1L, "Old Title", "Old Desc", 1L);
        TaskDto updateDto = new TaskDto("New Title", "New Desc", 1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

        Task result = taskService.updateTask(1L, updateDto);

        assertEquals("New Title", result.getTitle());
        assertEquals("New Desc", result.getDescription());
        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    void deleteTask_ShouldDeleteTask_WhenExists() {
        Task task = new Task(1L, "Test Task", "Test Description", 1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        List<Task> tasks = List.of(
                new Task(1L, "Task 1", "Desc 1", 1L),
                new Task(2L, "Task 2", "Desc 2", 1L)
        );
        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.getAllTasks();

        assertEquals(2, result.size());
        verify(taskRepository, times(1)).findAll();
    }
}
