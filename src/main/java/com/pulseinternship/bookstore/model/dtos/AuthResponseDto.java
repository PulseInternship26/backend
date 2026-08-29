package com.pulseinternship.bookstore.model.dtos;

import com.pulseinternship.bookstore.model.enums.UserRole;

public record AuthResponseDto(
        String token,
        String email,
        UserRole role,
        long expiresIn
) {
}
