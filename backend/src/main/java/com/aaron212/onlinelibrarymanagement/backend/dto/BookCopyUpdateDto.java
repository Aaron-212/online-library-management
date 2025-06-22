package com.aaron212.onlinelibrarymanagement.backend.dto;

import com.aaron212.onlinelibrarymanagement.backend.model.BookCopy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookCopyUpdateDto(
        String barcode,
        BookCopy.Status status,
        BigDecimal purchasePrice,
        LocalDateTime purchaseTime,
        LocalDateTime lastMaintenance
) {
}