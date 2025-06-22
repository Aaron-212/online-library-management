package com.aaron212.onlinelibrarymanagement.backend.dto;

import java.time.LocalDateTime;

/**
 * Data transfer object for favorite API responses with minimal book information.
 */
public record FavoriteResponseDto(
        Long id, Long bookId, String bookTitle, String bookIsbn, String bookCoverURL, LocalDateTime createTime) {}
