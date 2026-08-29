package com.pulseinternship.bookstore.model.dtos;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

public record BookRequestDto(
        @NotBlank
        @Size(max = 255)
        String title,

        @NotBlank
        @Size(max = 255)
        String author,

        @NotBlank
        @Size(max = 255)
        String category,

        @NotNull
        @Positive
        BigDecimal price,

        @NotBlank
        @Size(max = 1000)
        String description,

        @NotBlank
        @Size(max = 255)
        String imageUrl
) {
}
