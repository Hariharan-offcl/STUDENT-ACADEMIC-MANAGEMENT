package com.college.academix.config;

import com.college.academix.model.Role;
import com.college.academix.model.User;
import com.college.academix.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create default admin user if not exists
        if (!userRepository.existsByEmail("admin@academix.college")) {
            User admin = User.builder()
                    .email("admin@academix.college")
                    .password(passwordEncoder.encode("admin123"))
                    .name("System Admin")
                    .role(Role.ADMIN)
                    .isFirstLogin(false)
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Default admin created: admin@academix.college / admin123");
        }
    }
}
