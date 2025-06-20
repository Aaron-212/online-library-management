package com.aaron212.onlinelibrarymanagement.backend.dto;

import com.aaron212.onlinelibrarymanagement.backend.model.Borrow;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BorrowDto(
        Long borrowId,
        Long userId,
        String username,
        Long copyId,
        String bookTitle,
        String isbn,
        LocalDateTime borrowTime,
        LocalDateTime returnTime,
        LocalDateTime actualReturnTime,
        Borrow.Status status,
        BigDecimal fine) {}
