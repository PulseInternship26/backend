package com.pulseinternship.bookstore.service;

import com.pulseinternship.bookstore.exception.AdminNotFoundException;
import com.pulseinternship.bookstore.exception.DuplicateEmailException;
import com.pulseinternship.bookstore.model.dtos.AdminRequestDto;
import com.pulseinternship.bookstore.model.dtos.UserResponseDto;
import com.pulseinternship.bookstore.model.entities.User;
import com.pulseinternship.bookstore.model.enums.UserRole;
import com.pulseinternship.bookstore.repository.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponseDto> getAllAdmins() {
        return userRepo.findAllByRole(UserRole.ADMIN).stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponseDto createAdmin(AdminRequestDto request) {
        if (userRepo.findByEmail(request.email()).isPresent()) {
            throw new DuplicateEmailException("Email already exists");
        }

        User admin = new User();
        admin.setEmail(request.email());
        admin.setPassword(passwordEncoder.encode(request.password()));
        admin.setPhone(request.phone());
        admin.setRole(UserRole.ADMIN);

        return toResponse(userRepo.save(admin));
    }

    public void deleteAdmin(Long id) {
        User admin = userRepo.findById(id)
                .filter(user -> user.getRole() == UserRole.ADMIN)
                .orElseThrow(() -> new AdminNotFoundException("Admin with id " + id + " not found"));
        userRepo.delete(admin);
    }

    private UserResponseDto toResponse(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }
}
