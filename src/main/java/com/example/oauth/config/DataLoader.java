package com.example.oauth.config;

import com.example.oauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) {
        try {
            // Create test users
            userService.createUser("user", "password", "user@example.com", Set.of("USER"));
            System.out.println("Created user: user / password");
            
            userService.createUser("admin", "admin", "admin@example.com", Set.of("USER", "ADMIN"));
            System.out.println("Created user: admin / admin");
            
        } catch (RuntimeException e) {
            System.out.println("Users already exist, skipping creation");
        }
    }
}
