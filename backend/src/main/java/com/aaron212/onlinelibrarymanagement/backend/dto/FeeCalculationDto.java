package com.aaron212.onlinelibrarymanagement.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FeeCalculationDto(
    @NotNull(message = "Borrow ID is required")
    @Positive(message = "Borrow ID must be positive")
    Long borrowId
) {} 
