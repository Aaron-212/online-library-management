package com.aaron212.onlinelibrarymanagement.backend.mapper;

import com.aaron212.onlinelibrarymanagement.backend.dto.BorrowDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BorrowResponseDto;
import com.aaron212.onlinelibrarymanagement.backend.model.Borrow;
import org.springframework.stereotype.Component;

@Component
public class BorrowMapper {

    public static final BorrowMapper INSTANCE = new BorrowMapper();

    public BorrowDto toBorrowDto(Borrow borrow) {
        if (borrow == null) {
            return null;
        }

        return new BorrowDto(
                borrow.getId(),
                borrow.getUser().getId(),
                borrow.getUser().getUsername(),
                borrow.getCopy().getId(),
                borrow.getCopy().getBook().getTitle(),
                borrow.getCopy().getBook().getIsbn(),
                borrow.getCopy().getBook().getCoverURL(),
                borrow.getBorrowTime(),
                borrow.getReturnTime(),
                borrow.getActualReturnTime(),
                borrow.getStatus(),
                borrow.getFine());
    }

    public BorrowResponseDto toBorrowResponseDto(Borrow borrow) {
        if (borrow == null) {
            return null;
        }

        return new BorrowResponseDto(
                borrow.getId(),
                borrow.getUser().getId(),
                borrow.getUser().getUsername(),
                borrow.getCopy().getId(),
                borrow.getCopy().getBook().getTitle(),
                borrow.getCopy().getBook().getIsbn(),
                borrow.getBorrowTime(),
                borrow.getReturnTime(),
                borrow.getStatus(),
                "" // Message will be set in controller
                );
    }
}
