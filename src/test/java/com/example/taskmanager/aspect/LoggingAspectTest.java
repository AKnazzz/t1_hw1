package com.example.taskmanager.aspect;

import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @Mock
    private Logger log;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private MethodSignature methodSignature;

    private LoggingAspect loggingAspect;

    @BeforeEach
    void setUp() {
        loggingAspect = new LoggingAspect(log);
    }

    @Test
    void logBeforeCreateTask_ShouldLogInfoMessage() {
        loggingAspect.logBeforeCreateTask();
        verify(log).info("Attempting to create a new task");
    }

    @Test
    void logAfterThrowing_ShouldLogError() {
        TaskNotFoundException ex = new TaskNotFoundException(1L);
        loggingAspect.logAfterThrowing(ex);
        verify(log).error("Task not found: {}", "Task not found with id: 1");
    }

    @Test
    void logAfterReturningGetTask_ShouldLogTask() {
        Task task = new Task(1L, "Test", "Desc", 1L);
        loggingAspect.logAfterReturningGetTask(task);
        verify(log).info("Successfully retrieved task: {}", task);
    }

    @Test
    void logMethodExecutionTime_ShouldLogExecutionTime() throws Throwable {
        // Arrange
        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn("testMethod");
        when(proceedingJoinPoint.proceed()).thenReturn("result");

        // Act
        Object result = loggingAspect.logMethodExecutionTime(proceedingJoinPoint);

        // Assert
        assertEquals("result", result);
        verify(log).info(eq("Method {} executed in {} ms"), eq("testMethod"), anyLong());
    }

}
