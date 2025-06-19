package com.aaron212.onlinelibrarymanagement.backend.mapper;

import com.aaron212.onlinelibrarymanagement.backend.dto.FeeResponseDto;
import com.aaron212.onlinelibrarymanagement.backend.model.Borrow;
import org.springframework.stereotype.Component;

@Component
public class FeeMapper {
    
    public static final FeeMapper INSTANCE = new FeeMapper();
    
    public FeeResponseDto toFeeResponseDto(Borrow borrow, String message) {
        if (borrow == null) {
            return null;
        }
        
        return new FeeResponseDto(
            borrow.getId(),
            borrow.getUser().getId(),
            borrow.getUser().getUsername(),
            borrow.getCopy().getBook().getTitle(),
            borrow.getFine(),
            borrow.getStatus(),
            borrow.getReturnTime(),
            borrow.getActualReturnTime(),
            message
        );
    }
} 
