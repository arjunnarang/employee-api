package com.example.employeeapi.config;

import com.example.employeeapi.entity.User;
import com.example.employeeapi.entity.User.Role;
import com.example.employeeapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        createDefaultUsers();
    }

    private void createDefaultUsers() {
        // Create admin user if it doesn't exist
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("System")
                    .lastName("Admin")
                    .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER))
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            log.info("Default admin user created — username: admin, password: admin123");
        }

        // Create HR user
        if (!userRepository.existsByUsername("hrmanager")) {
            User hr = User.builder()
                    .username("hrmanager")
                    .email("hr@example.com")
                    .password(passwordEncoder.encode("hr12345"))
                    .firstName("HR")
                    .lastName("Manager")
                    .roles(Set.of(Role.ROLE_HR, Role.ROLE_USER))
                    .enabled(true)
                    .build();
            userRepository.save(hr);
            log.info("Default HR user created — username: hrmanager, password: hr12345");
        }

        // Create regular user
        if (!userRepository.existsByUsername("viewer")) {
            User viewer = User.builder()
                    .username("viewer")
                    .email("viewer@example.com")
                    .password(passwordEncoder.encode("view1234"))
                    .firstName("Read")
                    .lastName("Only")
                    .roles(Set.of(Role.ROLE_USER))
                    .enabled(true)
                    .build();
            userRepository.save(viewer);
            log.info("Default viewer user created — username: viewer, password: view1234");
        }
    }
}
