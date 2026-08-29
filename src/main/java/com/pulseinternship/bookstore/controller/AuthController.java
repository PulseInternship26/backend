package com.pulseinternship.bookstore.controller;

import com.pulseinternship.bookstore.model.dtos.AuthResponseDto;
import com.pulseinternship.bookstore.model.dtos.LoginRequestDto;
import com.pulseinternship.bookstore.model.dtos.RegisterRequestDto;
import com.pulseinternship.bookstore.model.dtos.UserResponseDto;
import com.pulseinternship.bookstore.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponseDto login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return authService.login(loginRequestDto);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        return authService.register(registerRequestDto);
    }
}
