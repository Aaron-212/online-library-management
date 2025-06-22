package com.aaron212.onlinelibrarymanagement.backend.dto;

import java.util.List;

/**
 * Data transfer object representing a full book with nested minimal DTOs for related entities.
 */
public record BookDto(
        Long id,
        String isbn,
        String title,
        String language,
        Integer availableQuantity,
        Integer totalQuantity,
        String description,
        List<AuthorDto> authors,
        List<PublisherDto> publishers,
        IndexCategoryDto indexCategory,
        String coverURL) {}
