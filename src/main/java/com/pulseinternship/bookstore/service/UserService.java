package com.pulseinternship.bookstore.service;

import com.pulseinternship.bookstore.exception.EmailNotFoundException;
import com.pulseinternship.bookstore.model.dtos.UserResponseDto;
import com.pulseinternship.bookstore.model.entities.User;
import com.pulseinternship.bookstore.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class UserService {
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public UserResponseDto getCurrentUser(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("User with email " + email + " not found"));
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }
}
