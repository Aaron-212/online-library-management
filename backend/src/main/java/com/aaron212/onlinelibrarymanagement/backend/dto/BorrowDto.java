package com.aaron212.onlinelibrarymanagement.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BorrowDto(
        Long id,
        Long userId,
        String username,
        Long copyId,
        String bookTitle,
        String isbn,
        LocalDateTime borrowTime,
        LocalDateTime returnTime,
        LocalDateTime actualReturnTime,
        String status,
        BigDecimal fine
) {} 
