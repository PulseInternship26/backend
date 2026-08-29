package com.pulseinternship.bookstore.repository;

import com.pulseinternship.bookstore.model.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepo extends JpaRepository<Book, Long> {
}
