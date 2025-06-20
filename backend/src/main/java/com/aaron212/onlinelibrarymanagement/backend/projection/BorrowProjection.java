package com.aaron212.onlinelibrarymanagement.backend.projection;

import com.aaron212.onlinelibrarymanagement.backend.model.Borrow;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface BorrowProjection {
    Long getId();

    Long getUserId();

    String getUsername();

    Long getCopyId();

    String getBookTitle();

    String getIsbn();

    LocalDateTime getBorrowTime();

    LocalDateTime getReturnTime();

    LocalDateTime getActualReturnTime();

    Borrow.Status getStatus();

    BigDecimal getFine();
}
