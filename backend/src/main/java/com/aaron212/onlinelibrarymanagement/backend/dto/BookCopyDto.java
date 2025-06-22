package com.aaron212.onlinelibrarymanagement.backend.dto;

import com.aaron212.onlinelibrarymanagement.backend.model.BookCopy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookCopyDto(
        Long id,
        Long bookId,
        String bookTitle,
        String bookIsbn,
        String barcode,
        BookCopy.Status status,
        BigDecimal purchasePrice,
        LocalDateTime purchaseTime,
        LocalDateTime lastMaintenance,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {
}