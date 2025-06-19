package com.aaron212.onlinelibrarymanagement.backend.dto;

public record BookUpdateDto(
        String title,
        String indexCategory,
        String location
) {}
