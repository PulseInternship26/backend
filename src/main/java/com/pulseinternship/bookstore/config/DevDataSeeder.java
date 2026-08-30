package com.pulseinternship.bookstore.config;

import com.pulseinternship.bookstore.model.entities.User;
import com.pulseinternship.bookstore.model.enums.UserRole;
import com.pulseinternship.bookstore.repository.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {
    private static final String ADMIN_EMAIL = "admin@bookstore.local";
    private static final String USER_EMAIL = "user@bookstore.local";

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final String adminPassword;
    private final String userPassword;

    public DevDataSeeder(
            UserRepo userRepo,
            PasswordEncoder passwordEncoder,
            @Value("${SEED_ADMIN_PASSWORD}") String adminPassword,
            @Value("${SEED_USER_PASSWORD}") String userPassword
    ) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.adminPassword = adminPassword;
        this.userPassword = userPassword;
    }

    @Override
    public void run(String... args) {
        seedUser(ADMIN_EMAIL, adminPassword, "01000000000", UserRole.ADMIN);
        seedUser(USER_EMAIL, userPassword, "01100000000", UserRole.USER);
    }

    private void seedUser(String email, String password, String phone, UserRole role) {
        User user = userRepo.findByEmail(email).orElseGet(User::new);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setRole(role);
        userRepo.save(user);
    }
}
