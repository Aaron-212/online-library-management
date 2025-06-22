package com.aaron212.onlinelibrarymanagement.backend.controller;

import com.aaron212.onlinelibrarymanagement.backend.dto.BookCopyCreateDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BookCopyDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BookCopyUpdateDto;
import com.aaron212.onlinelibrarymanagement.backend.model.BookCopy;
import com.aaron212.onlinelibrarymanagement.backend.service.BookCopyService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/book-copies")
@Tag(name = "Book Copies", description = "Book copy management endpoints")
public class BookCopyController {

    private final BookCopyService bookCopyService;

    public BookCopyController(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    @Operation(
            summary = "Create a new book copy",
            description = "Creates a new copy of an existing book",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Book copy created successfully",
                        content = @Content(schema = @Schema(implementation = BookCopyDto.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid book copy data",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Book not found",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "409",
                        description = "Book copy with barcode already exists",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createBookCopy(@Valid @RequestBody BookCopyCreateDto createDto) {
        try {
            BookCopyDto created = bookCopyService.createBookCopy(createDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get book copy by ID", description = "Retrieves a specific book copy by its ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Book copy found",
                        content = @Content(schema = @Schema(implementation = BookCopyDto.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Book copy not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookCopyById(
            @Parameter(description = "Book copy ID", required = true, example = "1") 
            @PathVariable @Positive Long id) {
        try {
            BookCopyDto copy = bookCopyService.getBookCopyById(id);
            return ResponseEntity.ok(copy);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Update book copy",
            description = "Updates an existing book copy with the provided details",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Book copy updated successfully",
                        content = @Content(schema = @Schema(implementation = BookCopyDto.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid book copy data",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Book copy not found",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "409",
                        description = "Book copy with barcode already exists",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBookCopy(
            @Parameter(description = "Book copy ID", required = true, example = "1") 
            @PathVariable @Positive Long id,
            @Valid @RequestBody BookCopyUpdateDto updateDto) {
        try {
            BookCopyDto updated = bookCopyService.updateBookCopy(id, updateDto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Update book copy status",
            description = "Updates only the status of a book copy",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Book copy status updated successfully",
                        content = @Content(schema = @Schema(implementation = BookCopyDto.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid status or business logic error",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Book copy not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateBookCopyStatus(
            @Parameter(description = "Book copy ID", required = true, example = "1") 
            @PathVariable @Positive Long id,
            @RequestBody Map<String, String> statusUpdate) {
        try {
            String statusStr = statusUpdate.get("status");
            BookCopy.Status status = BookCopy.Status.valueOf(statusStr);
            BookCopyDto updated = bookCopyService.updateBookCopyStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid status value"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Delete book copy",
            description = "Deletes a book copy by its ID",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Book copy deleted successfully"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Cannot delete borrowed book copy",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Book copy not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBookCopy(
            @Parameter(description = "Book copy ID", required = true, example = "1") 
            @PathVariable @Positive Long id) {
        try {
            bookCopyService.deleteBookCopy(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get all available book copies", description = "Retrieves all book copies that are available for borrowing")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Available book copies retrieved successfully",
                        content = @Content(schema = @Schema(implementation = List.class)))
            })
    @GetMapping("/available")
    public ResponseEntity<List<BookCopyDto>> getAllAvailableCopies() {
        List<BookCopyDto> availableCopies = bookCopyService.getAllAvailableCopies();
        return ResponseEntity.ok(availableCopies);
    }
}