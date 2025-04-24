package com.example.apiloggingstarter.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiLoggingPropertiesTest {

    @Test
    @DisplayName("Проверка начальных значений настроек логирования")
    void shouldHaveDefaultValues() {
        ApiLoggingProperties properties = new ApiLoggingProperties();

        assertTrue(properties.isEnabled());
        assertEquals("INFO", properties.getLevel());
        assertTrue(properties.isLogRequest());
        assertTrue(properties.isLogResponse());
        assertTrue(properties.isLogExecutionTime());
    }
}
