package com.example.taskmanager.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@SpringBootTest
@AutoConfigureMockMvc
class TaskValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createTaskWithEmptyTitle_ShouldFailValidation() throws Exception {
        String invalidTaskJson = "{\"title\":\"\",\"description\":\"Desc\",\"userId\":1}";

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTaskJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTaskWithoutUserId_ShouldFailValidation() throws Exception {
        String invalidTaskJson = "{\"title\":\"Test\",\"description\":\"Desc\"}";

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTaskJson))
                .andExpect(status().isBadRequest());
    }
}