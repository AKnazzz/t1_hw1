package com.example.taskmanager.aspect;

import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @Mock
    private Logger log;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private MethodSignature methodSignature;

    @Mock
    private Signature signature;

    private LoggingAspect loggingAspect;

    @BeforeEach
    void setUp() {
        loggingAspect = new LoggingAspect(log);
    }

    @Test
    void logBeforeCreateTask_ShouldLogInfoMessage() {
        // Act
        loggingAspect.logBeforeCreateTask();

        // Assert
        verify(log).info("Attempting to create a new task");
        verifyNoMoreInteractions(log);
    }

    @Test
    void logAfterThrowing_ShouldLogErrorWhenTaskNotFoundException() {
        // Arrange
        TaskNotFoundException ex = new TaskNotFoundException(1L);

        // Act
        loggingAspect.logAfterThrowing(ex);

        // Assert
        verify(log).error("Task not found: {}", ex.getMessage());
        verifyNoMoreInteractions(log);
    }


    @Test
    void logAfterReturningGetTask_ShouldLogTaskInfo() {
        // Arrange
        Task task = new Task(1L, "Test", "Desc", 1L, TaskStatus.PENDING);

        // Act
        loggingAspect.logAfterReturningGetTask(task);

        // Assert
        verify(log).info("Successfully retrieved task: {}", task);
        verifyNoMoreInteractions(log);
    }

    @Test
    void logMethodExecutionTime_ShouldLogExecutionTimeAndReturnResult() throws Throwable {
        // Arrange
        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn("testMethod");
        when(proceedingJoinPoint.proceed()).thenReturn("successResult");

        // Act
        Object result = loggingAspect.logMethodExecutionTime(proceedingJoinPoint);

        // Assert
        assertEquals("successResult", result);
        verify(log).info(eq("Method {} executed in {} ms"), eq("testMethod"), anyLong());
        verify(proceedingJoinPoint).proceed();
    }

}