package com.aaron212.onlinelibrarymanagement.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "Data transfer object for creating a new comment")
public record CommentCreateDto(
        @Schema(description = "Book ID to comment on", example = "1", required = true)
                @NotNull(message = "Book ID is required")
                @Positive(message = "Book ID must be positive")
                Long bookId,
        @Schema(description = "Comment content", example = "This is a great book!", required = true)
                @NotBlank(message = "Comment content is required")
                @Size(min = 10, max = 1000, message = "Comment content must be between 10 and 1000 characters")
                String content,
        @Schema(description = "Rating (0.0 to 5.0)", example = "4.5", required = false)
                @DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
                @DecimalMax(value = "5.0", message = "Rating must be at most 5.0")
                @Digits(
                        integer = 1,
                        fraction = 1,
                        message = "Rating must have at most 1 digit before and 1 digit after decimal point")
                BigDecimal rating) {}
