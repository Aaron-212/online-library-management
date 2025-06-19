package com.aaron212.onlinelibrarymanagement.backend.mapper;

import com.aaron212.onlinelibrarymanagement.backend.dto.BorrowDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BorrowResponseDto;
import com.aaron212.onlinelibrarymanagement.backend.model.Borrow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BorrowMapper {
    BorrowMapper INSTANCE = Mappers.getMapper(BorrowMapper.class);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "copy.id", target = "copyId")
    @Mapping(source = "copy.book.title", target = "bookTitle")
    @Mapping(source = "copy.book.isbn", target = "isbn")
    @Mapping(source = "status", target = "status")
    BorrowDto toBorrowDto(Borrow borrow);

    @Mapping(source = "id", target = "borrowId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "copy.id", target = "copyId")
    @Mapping(source = "copy.book.title", target = "bookTitle")
    @Mapping(source = "copy.book.isbn", target = "isbn")
    @Mapping(source = "status", target = "status")
    @Mapping(target = "message", ignore = true)
    BorrowResponseDto toBorrowResponseDto(Borrow borrow);
} 
