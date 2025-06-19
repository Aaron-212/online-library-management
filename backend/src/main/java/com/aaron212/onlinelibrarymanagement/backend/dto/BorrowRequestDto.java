package com.aaron212.onlinelibrarymanagement.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BorrowRequestDto(
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    Long userId,
    
    @NotNull(message = "Copy ID is required")
    @Positive(message = "Copy ID must be positive")
    Long copyId
) {} 
