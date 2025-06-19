package com.aaron212.onlinelibrarymanagement.backend.dto;

public record UserLoginDto(
        String usernameOrEmail,
        String password
) {}
