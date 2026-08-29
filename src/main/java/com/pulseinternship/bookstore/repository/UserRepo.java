package com.pulseinternship.bookstore.repository;

import com.pulseinternship.bookstore.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import com.pulseinternship.bookstore.model.enums.UserRole;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findAllByRole(UserRole role);
}
