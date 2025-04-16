package com.example.apiloggingstarter.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "api.logging")
public class ApiLoggingProperties {
    /**
     * Включить/выключить логирование API
     */
    private boolean enabled = true;

    /**
     * Уровень логирования (INFO, DEBUG, WARN, ERROR)
     */
    private String level = "INFO";

    /**
     * Логировать входящие запросы
     */
    private boolean logRequest = true;

    /**
     * Логировать исходящие ответы
     */
    private boolean logResponse = true;

    /**
     * Логировать время выполнения метода
     */
    private boolean logExecutionTime = true;
}