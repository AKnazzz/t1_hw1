package com.example.taskmanager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration.class,
        org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration.class
})
public class TaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }

}
