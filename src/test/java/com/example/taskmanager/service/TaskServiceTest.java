package com.example.taskmanager.service;

import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.model.dto.TaskDto;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    @DisplayName("Создание задачи должно возвращать сохраненную задачу")
    void createTask_ShouldReturnSavedTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task created = taskService.createTask(taskDto);

        assertNotNull(created);
        assertEquals("Test Task", created.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Получение задачи по ID, когда задача существует")
    void getTaskById_WhenTaskExists_ShouldReturnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task found = taskService.getTaskById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    @DisplayName("Получение задачи по ID, когда задача не существует")
    void getTaskById_WhenTaskNotExists_ShouldThrowException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));
    }

    @Test
    @DisplayName("Обновление задачи должно отправлять сообщение Kafka, если статус изменился")
    void updateTask_WhenStatusChanged_ShouldSendKafkaMessage() {
        // Arrange
        Task existingTask = new Task(1L, "Test Task", "Description", 1L, TaskStatus.PENDING);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

        // Act
        taskService.updateTask(1L, taskDto);

        // Assert
        verify(taskStatusProducer, times(1)).sendTaskStatusUpdate(1L, TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Обновление задачи не должно отправлять сообщение Kafka, если статус не изменился")
    void updateTask_WhenStatusNotChanged_ShouldNotSendKafkaMessage() {
        taskDto.setStatus(TaskStatus.PENDING);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.updateTask(1L, taskDto);

        verify(taskStatusProducer, never()).sendTaskStatusUpdate(anyLong(), any());
    }

    @Test
    @DisplayName("Удаление существующей задачи")
    void deleteTask_ShouldDeleteExistingTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L);

        verify(taskRepository).delete(task);
    }

    @Test
    @DisplayName("Удаление задачи, которая не существует, должно выбрасывать исключение")
    void deleteTask_WhenTaskNotExists_ShouldThrowException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(1L));
        verify(taskRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Получение всех задач")
    void getAllTasks_ShouldReturnListOfTasks() {
        List<Task> tasks = List.of(
                new Task(1L, "Task 1", "Description 1", 1L, TaskStatus.PENDING),
                new Task(2L, "Task 2", "Description 2", 2L, TaskStatus.COMPLETED)
        );

        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.getAllTasks();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(taskRepository).findAll();
    }
}
