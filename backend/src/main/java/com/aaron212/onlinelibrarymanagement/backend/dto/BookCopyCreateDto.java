package com.aaron212.onlinelibrarymanagement.backend.dto;

import com.aaron212.onlinelibrarymanagement.backend.model.BookCopy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookCopyCreateDto(
        @NotNull(message = "Book ID is required")
        @Positive(message = "Book ID must be positive")
        Long bookId,

        @NotBlank(message = "Barcode is required")
        String barcode,

        @NotNull(message = "Status is required")
        BookCopy.Status status,

        BigDecimal purchasePrice,
        LocalDateTime purchaseTime
) {
}