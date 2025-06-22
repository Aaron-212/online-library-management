package com.aaron212.onlinelibrarymanagement.backend.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Data transfer object for creating a new favorite.
 */
public record FavoriteCreateDto(@NotNull(message = "Book ID is required") Long bookId) {}
