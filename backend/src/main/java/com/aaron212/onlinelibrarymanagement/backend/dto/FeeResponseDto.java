package com.aaron212.onlinelibrarymanagement.backend.dto;

import com.aaron212.onlinelibrarymanagement.backend.model.Borrow;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FeeResponseDto(
    Long borrowId,
    Long userId,
    String username,
    String bookTitle,
    BigDecimal fineAmount,
    Borrow.Status status,
    LocalDateTime dueDate,
    LocalDateTime actualReturnDate,
    String message
) {} 
