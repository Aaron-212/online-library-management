package com.aaron212.onlinelibrarymanagement.backend.dto;

public record BookCreateDto(
        String isbn,
        String title,
        String indexCategory,
        String location
) {}
