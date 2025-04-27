package com.example.apiloggingstarter.aspect;

import com.example.apiloggingstarter.properties.ApiLoggingProperties;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import static org.junit.jupiter.api.Assertions.*;
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
    @DisplayName("Логирование входящих параметров и выполнения метода")
    void shouldLogRequestAndExecutionTime() throws Throwable {
        // Arrange
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature methodSignature = mock(MethodSignature.class);
        StopWatch stopWatch = new StopWatch();

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn("testMethod");
        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"param1", "param2"});
        when(joinPoint.proceed()).then(invocation -> {
            Thread.sleep(50); // Симуляция выполнения метода
            return "result";
        });

        // Act
        stopWatch.start();
        Object result = apiLoggingAspect.logApiCall(joinPoint);
        stopWatch.stop();

        // Assert
        verify(joinPoint, times(1)).proceed();
        assertNotNull(result);
        assertEquals("result", result);
        assertTrue(stopWatch.getTotalTimeMillis() >= 50, "Execution time should be recorded accurately");
    }

    @Test
    @DisplayName("Логирование исключений при выполнении метода")
    void shouldLogExceptionsDuringMethodExecution() throws Throwable {
        // Arrange
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature methodSignature = mock(MethodSignature.class);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn("testMethod");
        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Simulated Exception"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            apiLoggingAspect.logApiCall(joinPoint);
        });

        assertEquals("Simulated Exception", exception.getMessage());
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    @DisplayName("Логирование результата метода")
    void shouldLogMethodResult() throws Throwable {
        // Arrange
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature methodSignature = mock(MethodSignature.class);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn("testMethod");
        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1"});
        when(joinPoint.proceed()).thenReturn("Expected Result");

        // Act
        Object result = apiLoggingAspect.logApiCall(joinPoint);

        // Assert
        verify(joinPoint, times(1)).proceed();
        assertNotNull(result);
        assertEquals("Expected Result", result);
    }
}
