package com.pulseinternship.bookstore.service;

import com.pulseinternship.bookstore.exception.DuplicateEmailException;
import com.pulseinternship.bookstore.exception.EmailNotFoundException;
import com.pulseinternship.bookstore.exception.NonMatchingPasswordsException;
import com.pulseinternship.bookstore.model.dtos.AuthResponseDto;
import com.pulseinternship.bookstore.model.dtos.LoginRequestDto;
import com.pulseinternship.bookstore.model.dtos.RegisterRequestDto;
import com.pulseinternship.bookstore.model.dtos.UserResponseDto;
import com.pulseinternship.bookstore.model.entities.User;
import com.pulseinternship.bookstore.model.enums.UserRole;
import com.pulseinternship.bookstore.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserResponseDto register(RegisterRequestDto registerRequestDto) {
        if(!registerRequestDto.password().equals(registerRequestDto.confirmPassword())){
            throw new NonMatchingPasswordsException("Passwords do not match");
        }
        if (userRepo.findByEmail(registerRequestDto.email()).isPresent()) {
            throw new DuplicateEmailException("Email already exists");
        }
        User user = new User();
        user.setEmail(registerRequestDto.email());
        user.setPassword(passwordEncoder.encode(registerRequestDto.password()));
        user.setPhone(registerRequestDto.phone());
        user.setRole(UserRole.USER);
        User savedUser = userRepo.save(user);
        return new UserResponseDto(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getRole()
        );
    }

    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication =
                authenticationManager.authenticate(
                        UsernamePasswordAuthenticationToken.unauthenticated(
                                loginRequestDto.email(),
                                loginRequestDto.password()
                        )
                );
        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        UserRole role = userRepo.findByEmail(userDetails.getUsername())
                .map(User::getRole)
                .orElseThrow(() -> new EmailNotFoundException("Email not found: " + userDetails.getUsername()));
        String token = jwtService.generateToken(userDetails);
        return new AuthResponseDto(
                token,
                userDetails.getUsername(),
                role,
                jwtService.getTtlSeconds()
        );
    }
}
