package com.aaron212.onlinelibrarymanagement.backend.controller;

import com.aaron212.onlinelibrarymanagement.backend.dto.AuthorDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BookCreateDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BookDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BookSummaryDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BookUpdateDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.IndexCategoryDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.PublisherDto;
import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.model.BookCopy;
import com.aaron212.onlinelibrarymanagement.backend.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@RestController
@RequestMapping("/api/v1/books")
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@Tag(name = "Books", description = "Book management endpoints")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(
            summary = "Create a new book",
            description = "Creates a new book with the provided details",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Book created successfully",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid book data",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "409",
                        description = "Book with ISBN already exists",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createBook(@Valid @RequestBody BookCreateDto bookCreateDto) {
        try {
            bookService.createBook(bookCreateDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Book created successfully", "isbn", bookCreateDto.isbn()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Failed to create book"));
        }
    }

    @Operation(summary = "Get all books summary", description = "Retrieves a paginated list of book summaries with essential information")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Book summaries retrieved successfully",
                        content = @Content(schema = @Schema(implementation = Page.class)))
            })
    @GetMapping
    public ResponseEntity<Page<BookSummaryDto>> getAllBooksSummaryEndpoint(
            @Parameter(description = "Pagination parameters") @ParameterObject Pageable pageable) {
        Page<BookSummaryDto> books = bookService.getAllBooksSummaryPaged(pageable);
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "Get book by ID", description = "Retrieves a specific book by its ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Book found",
                        content = @Content(schema = @Schema(implementation = BookDto.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Book not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(
            @Parameter(description = "Book ID", required = true, example = "1") @PathVariable @Positive Long id) {
        Optional<Book> bookOpt = bookService.getBookById(id);
        if (bookOpt.isPresent()) {
            BookDto dto = convertToBookDto(bookOpt.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Book not found"));
        }
    }

    @Operation(summary = "Get book by ISBN", description = "Retrieves a specific book by its ISBN")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Book found",
                        content = @Content(schema = @Schema(implementation = BookDto.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Book not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<?> getBookByIsbn(
            @Parameter(description = "Book ISBN", required = true, example = "978-3-16-148410-0")
                    @PathVariable
                    @NotBlank
                    String isbn) {
        Optional<Book> bookOpt = bookService.findByIsbn(isbn);
        if (bookOpt.isPresent()) {
            BookDto dto = convertToBookDto(bookOpt.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Book not found"));
        }
    }

    @Operation(
            summary = "Update book",
            description = "Updates an existing book with the provided details",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Book updated successfully",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid book data",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Book not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(
            @Parameter(description = "Book ID", required = true, example = "1") @PathVariable @Positive Long id,
            @Valid @RequestBody BookUpdateDto bookUpdateDto) {
        try {
            bookService.updateBook(id, bookUpdateDto);
            return ResponseEntity.ok(Map.of("message", "Book updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Book not found"));
        }
    }

    @Operation(
            summary = "Delete book",
            description = "Deletes a book by its ID",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
                @ApiResponse(
                        responseCode = "404",
                        description = "Book not found",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Cannot delete book with active borrowings",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(
            @Parameter(description = "Book ID", required = true, example = "1") @PathVariable @Positive Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Book not found"));
        }
    }

    @Operation(summary = "Search books", description = "Searches for books by keyword in title, author, or ISBN")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Search completed successfully",
                        content = @Content(schema = @Schema(implementation = List.class)))
            })
    @GetMapping("/search")
    public ResponseEntity<Page<BookDto>> searchBooks(
            @Parameter(description = "Search keyword", required = true, example = "java programming")
                    @RequestParam
                    @NotBlank
                    String keyword,
            @Parameter(description = "Pagination parameters") @ParameterObject Pageable pageable) {
        Page<Book> booksPage = bookService.searchBooksPaged(keyword, pageable);
        Page<BookDto> dtoPage = booksPage.map(this::convertToBookDto);
        return ResponseEntity.ok(dtoPage);
    }

    @Operation(
            summary = "Check if book exists by ISBN",
            description = "Checks whether a book with the given ISBN exists in the system")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Check completed successfully",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/exists/isbn/{isbn}")
    public ResponseEntity<Map<String, Boolean>> existsByIsbn(
            @Parameter(description = "Book ISBN", required = true, example = "978-3-16-148410-0")
                    @PathVariable
                    @NotBlank
                    String isbn) {
        boolean exists = bookService.existsByIsbn(isbn);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @Operation(summary = "Get book copies", description = "Retrieves all copies of a specific book")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Book copies retrieved successfully",
                        content = @Content(schema = @Schema(implementation = List.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Book not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/{id}/copies")
    public ResponseEntity<?> getBookCopies(
            @Parameter(description = "Book ID", required = true, example = "1") @PathVariable @Positive Long id) {
        try {
            List<BookCopy> copies = bookService.getBookCopies(id);
            return ResponseEntity.ok(copies);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Book not found"));
        }
    }

    private BookDto convertToBookDto(Book book) {
        // Authors
        List<AuthorDto> authorDtos = book.getAuthors() != null ?
                book.getAuthors().stream()
                        .map(rel -> new AuthorDto(rel.getAuthor().getId(), rel.getAuthor().getName()))
                        .toList() : List.of();

        // Publishers
        List<PublisherDto> publisherDtos = book.getPublishers() != null ?
                book.getPublishers().stream()
                        .map(rel -> new PublisherDto(rel.getPublisher().getId(), rel.getPublisher().getName()))
                        .toList() : List.of();

        // Category
        IndexCategoryDto categoryDto = null;
        if (book.getIndexCategory() != null) {
            categoryDto = new IndexCategoryDto(
                    book.getIndexCategory().getId(),
                    book.getIndexCategory().getName());
        }

        int available = bookService.getAvailableCopiesCount(book);
        int total = bookService.getTotalCopiesCount(book);

        return new BookDto(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getLanguage(),
                available,
                total,
                book.getDescription(),
                authorDtos,
                publisherDtos,
                categoryDto,
                book.getCoverURL()
        );
    }
}
