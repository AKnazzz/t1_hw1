package com.example.apiloggingstarter.configuration;

import com.example.apiloggingstarter.aspect.ApiLoggingAspect;
import com.example.apiloggingstarter.properties.ApiLoggingProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {ApiLoggingAutoConfiguration.class})
class ApiLoggingAutoConfigurationTest {

    @Autowired
    private ApiLoggingProperties apiLoggingProperties;

    @Autowired
    private ApiLoggingAspect apiLoggingAspect;

    @Test
    @DisplayName("Проверка регистрации ApiLoggingAspect")
    void shouldRegisterApiLoggingAspect() {
        // Проверяем, что бин ApiLoggingAspect зарегистрирован и не null
        assertThat(apiLoggingAspect).isNotNull();
    }

    @Test
    @DisplayName("Проверка корректной загрузки свойств логирования")
    void shouldLoadApiLoggingProperties() {
        // Проверяем, что настройки ApiLoggingProperties загружены корректно
        assertThat(apiLoggingProperties).isNotNull();
        assertThat(apiLoggingProperties.isEnabled()).isTrue();
        assertThat(apiLoggingProperties.getLevel()).isEqualTo("INFO");
        assertThat(apiLoggingProperties.isLogRequest()).isTrue();
        assertThat(apiLoggingProperties.isLogResponse()).isTrue();
        assertThat(apiLoggingProperties.isLogExecutionTime()).isTrue();
    }
}
