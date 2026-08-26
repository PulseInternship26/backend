package com.pulseinternship.bookstore.model.dtos;

import com.pulseinternship.bookstore.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequestDto(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String password,

        @NotBlank
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
        String phone,

        @NotBlank
        UserRole role
) {
}
