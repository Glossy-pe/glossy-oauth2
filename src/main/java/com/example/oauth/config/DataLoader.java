package com.example.oauth.config;

import com.example.oauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserService userService;

    @Value("${APP_USER_USERNAME}")
    private String username;

    @Value("${APP_USER_PASSWORD}")
    private String password;

    @Value("${APP_USER_EMAIL}")
    private String email;

    @Override
    public void run(String... args) {
        try {
            userService.createUser(
                    username,
                    password,
                    email,
                    Set.of("USER")
            );

            System.out.println("Created user from ENV: " + username);

        } catch (RuntimeException e) {
            System.out.println("User already exists, skipping creation");
        }
    }
}
