package com.example.apiloggingstarter.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiLoggingPropertiesTest {

    @Test
    @DisplayName("Проверка значений по умолчанию")
    void shouldHaveDefaultValues() {
        // Act
        ApiLoggingProperties properties = new ApiLoggingProperties();

        // Assert
        assertThat(properties.isEnabled()).isTrue();
        assertThat(properties.getLevel()).isEqualTo("INFO");
        assertThat(properties.isLogRequest()).isTrue();
        assertThat(properties.isLogResponse()).isTrue();
        assertThat(properties.isLogExecutionTime()).isTrue();
    }

    @Test
    @DisplayName("Изменение значений настроек")
    void shouldAllowCustomValues() {
        // Arrange
        ApiLoggingProperties properties = new ApiLoggingProperties();

        // Act
        properties.setEnabled(false);
        properties.setLevel("DEBUG");
        properties.setLogRequest(false);
        properties.setLogResponse(false);
        properties.setLogExecutionTime(false);

        // Assert
        assertThat(properties.isEnabled()).isFalse();
        assertThat(properties.getLevel()).isEqualTo("DEBUG");
        assertThat(properties.isLogRequest()).isFalse();
        assertThat(properties.isLogResponse()).isFalse();
        assertThat(properties.isLogExecutionTime()).isFalse();
    }
}
