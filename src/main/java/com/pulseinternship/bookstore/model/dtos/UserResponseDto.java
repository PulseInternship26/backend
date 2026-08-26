package com.pulseinternship.bookstore.model.dtos;

import com.pulseinternship.bookstore.model.enums.UserRole;

public record UserResponseDto(
        Long id,
        String email,
        String phone,
        UserRole role
) {}