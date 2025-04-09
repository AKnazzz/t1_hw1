package com.example.taskmanager.aspect;

import com.example.taskmanager.exception.TaskNotFoundException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private final Logger log;

    public LoggingAspect() {
        this(LoggerFactory.getLogger(LoggingAspect.class));
    }

    LoggingAspect(Logger log) {
        this.log = log;
    }

    @Before("execution(* com.example.taskmanager.service.TaskService.createTask(..))")
    public void logBeforeCreateTask() {
        log.info("Attempting to create a new task");
    }

    @AfterThrowing(
            pointcut = "execution(* com.example.taskmanager.service.TaskService.*(..))",
            throwing = "ex"
    )
    public void logAfterThrowing(TaskNotFoundException ex) {
        log.error("Task not found: {}", ex.getMessage());
    }

    @AfterReturning(
            pointcut = "execution(* com.example.taskmanager.service.TaskService.getTaskById(..))",
            returning = "result"
    )
    public void logAfterReturningGetTask(Object result) {
        log.info("Successfully retrieved task: {}", result);
    }

    @Around("execution(* com.example.taskmanager.service.TaskService.*(..))")
    public Object logMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        log.info("Method {} executed in {} ms",
                joinPoint.getSignature().getName(),
                endTime - startTime);

        return result;
    }
}
