package com.example.taskmanager.service;

import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.model.dto.TaskDto;
import com.example.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final TaskStatusProducer taskStatusProducer;

    public Task createTask(TaskDto taskDto) {
        log.info("Creating a new task with title: {}", taskDto.getTitle());

        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setUserId(taskDto.getUserId());
        task.setStatus(taskDto.getStatus() != null ? taskDto.getStatus() : TaskStatus.PENDING);

        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with ID: {}", savedTask.getId());

        return savedTask;
    }

    public Task getTaskById(Long id) {
        log.info("Fetching task with ID: {}", id);

        return taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Task not found with ID: {}", id);
                    return new TaskNotFoundException(id);
                });
    }

    public Task updateTask(Long id, TaskDto taskDto) {
        log.info("Updating task with ID: {}", id);

        Task existingTask = getTaskById(id);
        boolean statusChanged = taskDto.getStatus() != null &&
                !taskDto.getStatus().equals(existingTask.getStatus());

        existingTask.setTitle(taskDto.getTitle());
        existingTask.setDescription(taskDto.getDescription());
        existingTask.setUserId(taskDto.getUserId());

        if (taskDto.getStatus() != null) {
            existingTask.setStatus(taskDto.getStatus());
        }

        Task updatedTask = taskRepository.save(existingTask);

        if (statusChanged) {
            log.info("Task status changed. Sending status update for Task ID: {}", id);
            taskStatusProducer.sendTaskStatusUpdate(id, updatedTask.getStatus());
        }

        log.info("Task with ID: {} updated successfully", id);
        return updatedTask;
    }

    public void deleteTask(Long id) {
        log.info("Deleting task with ID: {}", id);

        Task task = getTaskById(id);
        taskRepository.delete(task);

        log.info("Task with ID: {} deleted successfully", id);
    }

    public List<Task> getAllTasks() {
        log.info("Fetching all tasks");

        List<Task> tasks = taskRepository.findAll();
        log.info("Fetched {} tasks", tasks.size());

        return tasks;
    }
}

