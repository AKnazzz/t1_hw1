package com.example.apiloggingstarter.configuration;

import com.example.apiloggingstarter.aspect.ApiLoggingAspect;
import com.example.apiloggingstarter.configuration.ApiLoggingAutoConfiguration;
import com.example.apiloggingstarter.properties.ApiLoggingProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ApiLoggingAutoConfiguration.class})
class ApiLoggingAutoConfigurationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("Проверка включения логирования")
    void shouldEnableApiLogging() {
        assertNotNull(context.getBean(ApiLoggingAspect.class), "ApiLoggingAspect должен быть активен");
    }

    @Test
    @DisplayName("Проверка конфигурации свойств логирования")
    void shouldLoadProperties() {
        ApiLoggingProperties properties = context.getBean(ApiLoggingProperties.class);

        assertTrue(properties.isEnabled());
        assertEquals("INFO", properties.getLevel());
    }
}
