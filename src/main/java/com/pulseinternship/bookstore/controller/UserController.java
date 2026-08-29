package com.pulseinternship.bookstore.controller;

import com.pulseinternship.bookstore.model.dtos.UserResponseDto;
import com.pulseinternship.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponseDto getMe(Principal principal) {
        return userService.getCurrentUser(principal.getName());
    }
}
