package com.aaron212.onlinelibrarymanagement.backend.controller;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

import com.aaron212.onlinelibrarymanagement.backend.dto.CommentCreateDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.CommentDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.CommentUpdateDto;
import com.aaron212.onlinelibrarymanagement.backend.model.User;
import com.aaron212.onlinelibrarymanagement.backend.repository.UserRepository;
import com.aaron212.onlinelibrarymanagement.backend.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@Tag(name = "Comments", description = "Comment management endpoints")
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;

    public CommentController(CommentService commentService, UserRepository userRepository) {
        this.commentService = commentService;
        this.userRepository = userRepository;
    }

    @Operation(
            summary = "Create a new comment",
            description = "Creates a new comment for a book. Comments are initially pending and require approval.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Comment created successfully",
                        content = @Content(schema = @Schema(implementation = CommentDto.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid comment data or user has already commented on this book",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Book not found",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PostMapping
    public ResponseEntity<?> createComment(
            @Valid @RequestBody CommentCreateDto commentCreateDto, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }

        try {
            CommentDto createdComment = commentService.createComment(commentCreateDto, authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get comment by ID", description = "Retrieves a specific comment by its ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Comment found",
                        content = @Content(schema = @Schema(implementation = CommentDto.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Comment not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(
            @Parameter(description = "Comment ID", required = true, example = "1") @PathVariable @Positive Long id) {
        try {
            Optional<CommentDto> comment = commentService.getCommentById(id);
            if (comment.isPresent()) {
                return ResponseEntity.ok(comment.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Comment not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve comment"));
        }
    }

    @Operation(
            summary = "Get published comments for a book",
            description = "Retrieves all published comments for a specific book with pagination")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Comments retrieved successfully",
                        content = @Content(schema = @Schema(implementation = Page.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Book not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> getCommentsByBook(
            @Parameter(description = "Book ID", required = true, example = "1") @PathVariable @Positive Long bookId,
            @Parameter(description = "Pagination parameters") @ParameterObject Pageable pageable) {
        try {
            Page<CommentDto> comments = commentService.getPublishedCommentsByBook(bookId, pageable);
            return ResponseEntity.ok(comments);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Get comments by user",
            description = "Retrieves all published comments by a specific user with pagination",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Comments retrieved successfully",
                        content = @Content(schema = @Schema(implementation = Page.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getCommentsByUser(
            @Parameter(description = "Username", required = true, example = "john_doe") @PathVariable String username,
            @Parameter(description = "Pagination parameters") @ParameterObject Pageable pageable) {
        try {
            Page<CommentDto> comments = commentService.getCommentsByUser(username, pageable);
            return ResponseEntity.ok(comments);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Get current user's comments",
            description = "Retrieves all published comments by the currently authenticated user with pagination",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Comments retrieved successfully",
                        content = @Content(schema = @Schema(implementation = Page.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/my-comments")
    public ResponseEntity<?> getCurrentUserComments(
            @Parameter(description = "Pagination parameters") @ParameterObject Pageable pageable,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }

        try {
            Page<CommentDto> comments = commentService.getCommentsByUser(authentication.getName(), pageable);
            return ResponseEntity.ok(comments);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Get pending comments",
            description = "Retrieves all pending comments for moderation (admin only)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Pending comments retrieved successfully",
                        content = @Content(schema = @Schema(implementation = List.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "Access denied - admin role required",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingComments() {
        try {
            List<CommentDto> pendingComments = commentService.getPendingComments();
            return ResponseEntity.ok(pendingComments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve pending comments"));
        }
    }

    @Operation(
            summary = "Update comment",
            description =
                    "Updates an existing comment. Only the author can update their own comment and only if it's still pending.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Comment updated successfully",
                        content = @Content(schema = @Schema(implementation = CommentDto.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid comment data",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "User not authorized to update this comment",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Comment not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(
            @Parameter(description = "Comment ID", required = true, example = "1") @PathVariable @Positive Long id,
            @Valid @RequestBody CommentUpdateDto commentUpdateDto,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }

        try {
            CommentDto updatedComment = commentService.updateComment(id, commentUpdateDto, authentication.getName());
            return ResponseEntity.ok(updatedComment);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            } else if (e.getMessage().contains("not authorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Delete comment",
            description = "Deletes a comment. Authors can delete their own comments, admins can delete any comment.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "User not authorized to delete this comment",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Comment not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(
            @Parameter(description = "Comment ID", required = true, example = "1") @PathVariable @Positive Long id,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }

        try {
            // Get user role for authorization
            User user = userRepository
                    .findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            commentService.deleteComment(id, authentication.getName(), user.getRole());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            } else if (e.getMessage().contains("not authorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Approve comment",
            description = "Approves a pending comment (admin only)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Comment approved successfully",
                        content = @Content(schema = @Schema(implementation = CommentDto.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Comment cannot be approved",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "Access denied - admin role required",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Comment not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveComment(
            @Parameter(description = "Comment ID", required = true, example = "1") @PathVariable @Positive Long id) {
        try {
            CommentDto approvedComment = commentService.approveComment(id);
            return ResponseEntity.ok(approvedComment);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Reject comment",
            description = "Rejects a pending comment (admin only)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Comment rejected successfully"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Comment cannot be rejected",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "Access denied - admin role required",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Comment not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectComment(
            @Parameter(description = "Comment ID", required = true, example = "1") @PathVariable @Positive Long id) {
        try {
            commentService.rejectComment(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Get average rating for a book",
            description = "Retrieves the average rating for a specific book based on published comments")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Average rating retrieved successfully",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Book not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/book/{bookId}/average-rating")
    public ResponseEntity<?> getAverageRatingForBook(
            @Parameter(description = "Book ID", required = true, example = "1") @PathVariable @Positive Long bookId) {
        try {
            Optional<Double> averageRating = commentService.getAverageRatingForBook(bookId);
            long commentCount = commentService.getCommentCountForBook(bookId);

            Map<String, Object> response =
                    Map.of("averageRating", averageRating.orElse(0.0), "commentCount", commentCount);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
