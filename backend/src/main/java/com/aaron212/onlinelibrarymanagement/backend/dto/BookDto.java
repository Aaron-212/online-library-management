package com.aaron212.onlinelibrarymanagement.backend.dto;

import java.time.LocalDateTime;

public record BookDto(
        Long id,
        String isbn,
        String title,
        String categoryName,
        String categoryCode,
        String locationName,
        String locationCode,
        LocalDateTime createTime,
        int availableCopies,
        int totalCopies) {}
