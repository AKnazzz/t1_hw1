package com.example.apiloggingstarter.aspect;


import com.example.apiloggingstarter.properties.ApiLoggingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ApiLoggingAspect {
    private final ApiLoggingProperties properties;

    @Around("@annotation(com.example.apiloggingstarter.annotation.Loggable) || " +
            "@within(org.springframework.stereotype.Controller) || " +
            "@within(org.springframework.web.bind.annotation.RestController)")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!properties.isEnabled()) {
            return joinPoint.proceed();
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();

        // Логирование входящих параметров
        if (properties.isLogRequest()) {
            logAtLevel("Entering method {}.{}() with args: {}", className, methodName, joinPoint.getArgs());
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            // Логирование исключений
            logAtLevel("Exception in method {}.{}(): {}", className, methodName, e.getMessage());
            throw e;
        } finally {
            stopWatch.stop();
        }

        // Логирование времени выполнения
        if (properties.isLogExecutionTime()) {
            logAtLevel("Method {}.{}() executed in {} ms", className, methodName, stopWatch.getTotalTimeMillis());
        }

        // Логирование результата
        if (properties.isLogResponse()) {
            logAtLevel("Exiting method {}.{}() with result: {}", className, methodName, result);
        }

        return result;
    }

    private void logAtLevel(String message, Object... args) {
        switch (properties.getLevel().toUpperCase()) {
            case "DEBUG" -> log.debug(message, args);
            case "WARN" -> log.warn(message, args);
            case "ERROR" -> log.error(message, args);
            default -> log.info(message, args); // INFO по умолчанию
        }
    }
}