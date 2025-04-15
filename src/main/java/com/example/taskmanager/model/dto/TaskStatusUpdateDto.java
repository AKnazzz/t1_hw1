package com.example.taskmanager.model.dto;

import com.example.taskmanager.model.TaskStatus;
import org.antlr.v4.runtime.misc.NotNull;

public record TaskStatusUpdateDto(
        @NotNull Long taskId,
        @NotNull TaskStatus status,
        String userEmail
) {
}
