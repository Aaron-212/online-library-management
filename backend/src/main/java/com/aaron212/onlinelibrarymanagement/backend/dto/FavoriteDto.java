package com.aaron212.onlinelibrarymanagement.backend.dto;

import java.time.LocalDateTime;

/**
 * Data transfer object representing a user's favorite book with full book details.
 */
public record FavoriteDto(
        Long id,
        BookDto book,
        LocalDateTime createTime
) {
}