package com.aaron212.onlinelibrarymanagement.backend.dto;

import java.util.List;

public record BookUpdateDto(
        String title,
        String language,
        String description,
        String coverURL,
        String location,
        List<String> authorNames,
        List<String> publisherNames,
        String categoryName) {}
