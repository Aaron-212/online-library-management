package com.aaron212.onlinelibrarymanagement.backend.dto;

import java.util.List;

public record BookSummaryDto(
        Long id,
        String title,
        List<String> authors,
        List<String> publishers,
        String coverURL
) {
} 
