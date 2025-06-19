package com.aaron212.onlinelibrarymanagement.backend.dto;

import com.aaron212.onlinelibrarymanagement.backend.model.BookLocation;
import com.aaron212.onlinelibrarymanagement.backend.model.IndexCategory;

import java.util.List;

public record BookDto(
        Long id,
        String isbn,
        String title,
        List<String> authors,
        List<String> publishers,
        IndexCategory indexCategory,
        BookLocation location,
        String createTime) {}
