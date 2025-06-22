package com.aaron212.onlinelibrarymanagement.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserCreateDto(
        @NotBlank(message = "Username is required")
                @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
                @Pattern(
                        regexp = "^[a-zA-Z0-9_]+$",
                        message = "Username can only contain letters, numbers, and underscores")
                String username,
        @NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email,
        @NotBlank(message = "Password is required")
                @Size(min = 6, message = "Password must be at least 6 characters long")
                String password,
        @Pattern(regexp = "^(USER|ADMIN)$", message = "Role must be either USER or ADMIN") String role) {}
