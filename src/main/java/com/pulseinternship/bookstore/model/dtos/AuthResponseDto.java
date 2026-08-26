package com.pulseinternship.bookstore.model.dtos;

import com.pulseinternship.bookstore.model.enums.UserRole;

public record AuthResponseDto(
        String token,
        Long id,
        UserRole role,
        long expiresIn
) {
}
