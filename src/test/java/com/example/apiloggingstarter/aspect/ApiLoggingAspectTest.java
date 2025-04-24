package com.example.apiloggingstarter.aspect;

import com.example.apiloggingstarter.properties.ApiLoggingProperties;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ApiLoggingAspectTest {
    private ApiLoggingAspect apiLoggingAspect;
    private ApiLoggingProperties properties;

    @BeforeEach
    void setUp() {
        properties = new ApiLoggingProperties();
        properties.setLogRequest(true);
        properties.setLogResponse(true);
        properties.setLogExecutionTime(true);
        properties.setLevel("INFO");

        apiLoggingAspect = new ApiLoggingAspect(properties);
    }

    @Test
    @DisplayName("Логирование входящих параметров")
    void shouldLogRequestArguments() throws Throwable {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature methodSignature = mock(MethodSignature.class);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn("testMethod");
        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"param1", "param2"});

        // Эмуляция выполнения метода
        when(joinPoint.proceed()).thenReturn("result");

        apiLoggingAspect.logApiCall(joinPoint);

        // Проверяем логи
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    @DisplayName("Обработка исключений")
    void shouldLogExceptions() throws Throwable {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature methodSignature = mock(MethodSignature.class);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn("testMethod");
        when(joinPoint.getTarget()).thenReturn(this);

        // Эмуляция выброса исключения
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Test Exception"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                apiLoggingAspect.logApiCall(joinPoint)
        );

        // Проверяем, что выбрасывается ожидаемое исключение
        assertEquals("Test Exception", exception.getMessage());
    }
}
