package com.aaron212.onlinelibrarymanagement.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record BookUpdateDto(
        @NotBlank(message = "Title is required") String title,
        @NotBlank(message = "Index category is required") String indexCategory,
        @NotBlank(message = "Location is required") String location) {}
