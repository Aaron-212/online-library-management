package com.aaron212.onlinelibrarymanagement.backend.dto;

import java.time.LocalDateTime;

public record UserFullDto(String username, String email, String role, LocalDateTime createdAt) {
}
