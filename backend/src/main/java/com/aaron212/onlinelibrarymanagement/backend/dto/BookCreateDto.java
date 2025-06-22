package com.aaron212.onlinelibrarymanagement.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record BookCreateDto(
        @NotBlank(message = "ISBN is required") String isbn,
        @NotBlank(message = "Title is required") String title,
        @NotBlank(message = "Language is required") String language,
        String description,
        String coverURL,
        String location,
        @NotNull(message = "Author names are required") List<String> authorNames,
        List<String> publisherNames,
        @NotBlank(message = "Category name is required") String categoryName,
        @NotNull(message = "Total quantity is required") @Positive Integer totalQuantity) {}
