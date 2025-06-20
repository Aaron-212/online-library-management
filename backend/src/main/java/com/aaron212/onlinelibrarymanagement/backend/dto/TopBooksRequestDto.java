package com.aaron212.onlinelibrarymanagement.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record TopBooksRequestDto(
        @Min(value = 1, message = "Top count must be at least 1")
                @Max(value = 100, message = "Top count cannot exceed 100")
                Integer topCount) {
    public TopBooksRequestDto {
        if (topCount == null) {
            topCount = 10; // Default value
        }
    }
}
