package com.example.taskmanager.model.dto;

import com.example.taskmanager.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    private String title;
    private String description;
    private Long userId;
    private TaskStatus status;
}