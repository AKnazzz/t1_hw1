package com.example.taskmanager.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JavaMailSender mailSender;

    private static final String LONG_STRING = "A".repeat(256);

    @Test
    @DisplayName("Создание задачи без ID пользователя должно возвращать ошибку валидации")
    void createTaskWithoutUserId_ShouldFailValidation() throws Exception {
        String invalidTaskJson = "{\"title\":\"Test\",\"description\":\"Desc\"}";

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTaskJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userId").value("User ID is mandatory"));
    }

    @Test
    @DisplayName("Создание задачи с слишком длинным заголовком должно возвращать ошибку валидации")
    void createTaskWithTooLongTitle_ShouldFailValidation() throws Exception {
        String invalidTaskJson = "{\"title\":\"" + LONG_STRING + "\",\"description\":\"Desc\",\"userId\":1}";

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTaskJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title cannot exceed 255 characters"));
    }

    @Test
    @DisplayName("Создание задачи с пустым заголовком должно возвращать ошибку валидации")
    void createTaskWithEmptyTitle_ShouldFailValidation() throws Exception {
        String invalidTaskJson = "{\"title\":\"\",\"description\":\"Desc\",\"userId\":1}";

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTaskJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title is mandatory"));
    }

    @Test
    @DisplayName("Создание задачи с валидными данными должно возвращать статус 201 Created")
    void createTaskWithValidData_ShouldReturnCreated() throws Exception {
        String validTaskJson = "{\"title\":\"Valid Title\",\"description\":\"Desc\",\"userId\":1}";

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTaskJson))
                .andExpect(status().isCreated());
    }
}