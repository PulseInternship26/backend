package com.pulseinternship.bookstore.model.dtos;

import java.math.BigDecimal;

public record BookResponseDto(
        Long id,

        String title,

        String author,

        String category,

        BigDecimal price,

        String description,

        String imageUrl
) {
}
