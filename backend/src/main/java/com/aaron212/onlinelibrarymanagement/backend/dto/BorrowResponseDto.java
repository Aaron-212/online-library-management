package com.aaron212.onlinelibrarymanagement.backend.dto;

import java.time.LocalDateTime;

public record BorrowResponseDto(
        Long borrowId,
        Long userId,
        String username,
        Long copyId,
        String bookTitle,
        String isbn,
        LocalDateTime borrowTime,
        LocalDateTime returnTime,
        String status,
        String message
) {} 
