package com.stayease.userservice.service;

import com.stayease.userservice.dto.request.LoginRequest;
import com.stayease.userservice.dto.request.RegisterRequest;
import com.stayease.userservice.dto.response.AuthResponse;
import com.stayease.userservice.entity.User;
import com.stayease.userservice.exception.InvalidCredentialsException;
import com.stayease.userservice.exception.UserAlreadyExistsException;
import com.stayease.userservice.repository.UserRepository;
import com.stayease.userservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering user with email: {}", request.getEmail());

        // Check duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Email already registered: " + request.getEmail()
            );
        }


        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(User.Role.USER)
                .build();

        // Save
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        // Token generate
        String token = jwtUtil.generateToken(
                savedUser.getEmail(),
                savedUser.getRole().name()
        );

        return AuthResponse.builder()
                .token(token)
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .message("Registration successful")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        // User dhundho
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Invalid email or password"
                ));

        // Password check karo
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        log.info("User logged in successfully: {}", user.getEmail());

        // Token generate karo
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .message("Login successful")
                .build();
    }
}
