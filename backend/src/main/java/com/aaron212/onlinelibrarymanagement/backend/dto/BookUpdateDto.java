package com.aaron212.onlinelibrarymanagement.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookUpdateDto(
    @NotBlank(message = "Title is required") String title,
    @NotNull(message = "Index category ID is required") Long indexCategoryId,
    @NotNull(message = "Location ID is required") Long locationId
) {
} 
