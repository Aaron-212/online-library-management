package com.aaron212.onlinelibrarymanagement.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReserveRequestDto(
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    Long userId,
    
    @NotNull(message = "Book ID is required")
    @Positive(message = "Book ID must be positive")
    Long bookId
) {} 
