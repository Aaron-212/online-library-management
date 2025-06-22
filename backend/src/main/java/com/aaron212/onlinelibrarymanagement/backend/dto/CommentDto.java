package com.aaron212.onlinelibrarymanagement.backend.dto;

import com.aaron212.onlinelibrarymanagement.backend.model.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Data transfer object for comment responses")
public record CommentDto(
        @Schema(description = "Comment ID", example = "1") Long id,
        @Schema(description = "User ID who made the comment", example = "123") Long userId,
        @Schema(description = "Username who made the comment", example = "john_doe") String username,
        @Schema(description = "Book ID that was commented on", example = "456") Long bookId,
        @Schema(description = "Book title that was commented on", example = "The Great Gatsby") String bookTitle,
        @Schema(description = "Comment content", example = "This is a great book!") String content,
        @Schema(description = "Rating given (0.0 to 5.0)", example = "4.5") BigDecimal rating,
        @Schema(description = "When the comment was created", example = "2023-12-15T10:30:00") LocalDateTime createTime,
        @Schema(description = "Comment status", example = "PUBLISHED") Comment.Status status) {}
