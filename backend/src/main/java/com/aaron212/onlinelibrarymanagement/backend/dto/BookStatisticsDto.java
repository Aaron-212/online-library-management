package com.aaron212.onlinelibrarymanagement.backend.dto;

import java.util.List;

public record BookStatisticsDto(
    Long bookId,
    String title,
    String isbn,
    List<String> authors,
    List<String> publishers,
    Long borrowCount
) {} 
