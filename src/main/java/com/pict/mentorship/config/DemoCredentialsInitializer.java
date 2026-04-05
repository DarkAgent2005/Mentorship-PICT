package com.pict.mentorship.config;

import com.pict.mentorship.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DemoCredentialsInitializer implements CommandLineRunner {

    private final AuthService authService;

    public DemoCredentialsInitializer(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void run(String... args) {
        authService.ensureDemoCredential("riya.mentor@example.com", "Mentor@123");
        authService.ensureDemoCredential("aman.mentee@example.com", "Mentee@123");
        authService.ensureDemoCredential("admin@example.com", "Admin@123");
    }
}
