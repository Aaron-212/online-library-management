package com.aaron212.onlinelibrarymanagement.backend.dto;

/**
 * Aggregated statistics for the entire library collection.
 * <p>
 * The field names match the expectations of the front-end `BookStatisticsDto` interface
 * so that the JSON returned by the backend can be consumed directly.
 */
public record LibraryStatisticsDto(
        Long totalBooks,
        Long availableBooks,
        Long borrowedBooks,
        Long totalBorrows,
        Long activeBorrows,
        Long overdueBorrows) {
}