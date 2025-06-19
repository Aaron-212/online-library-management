package com.aaron212.onlinelibrarymanagement.backend.dto;

import com.aaron212.onlinelibrarymanagement.backend.model.BookLocation;
import com.aaron212.onlinelibrarymanagement.backend.model.IndexCategory;

public record BookDto(
        Long id,
        String isbn,
        String title,
        IndexCategory indexCategory,
        BookLocation location,
        String createTime) {}
