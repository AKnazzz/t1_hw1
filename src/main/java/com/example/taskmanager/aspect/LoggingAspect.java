package com.example.taskmanager.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("execution(* com.example.taskmanager.service.TaskService.createTask(..))")
    public void logBeforeCreateTask() {
        log.info("Attempting to create a new task");
    }

    @AfterThrowing(
            pointcut = "execution(* com.example.taskmanager.service.TaskService.*(..))",
            throwing = "ex"
    )
    public void logAfterThrowing(Exception ex) {
        log.error("Exception occurred: {}", ex.getMessage());
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
