package com.aaron212.onlinelibrarymanagement.backend.controller;

import com.aaron212.onlinelibrarymanagement.backend.dto.BookCreateDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BookDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BookUpdateDto;
import com.aaron212.onlinelibrarymanagement.backend.model.BookCopy;
import com.aaron212.onlinelibrarymanagement.backend.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@RestController
@RequestMapping("/books")
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Create a new book
     */
    @PostMapping("/create")
    public ResponseEntity<BookDto> createBook(@RequestBody BookCreateDto bookCreateDto) {
        try {
            BookDto createdBook = bookService.createBook(bookCreateDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createdBook);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .build();
        }
    }

    /**
     * Get all books with pagination
     */
    @GetMapping("/all")
    public ResponseEntity<Page<BookDto>> getAllBooks(Pageable pageable) {
        Page<BookDto> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(books);
    }

    /**
     * Get book by ID
     */
    @GetMapping("/getById")
    public ResponseEntity<BookDto> getBookById(@RequestParam Long id) {
        Optional<BookDto> book = bookService.getBookById(id);
        return book.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound()
                        .build());
    }

    /**
     * Get book by ISBN
     */
    @GetMapping("/getByIsbn")
    public ResponseEntity<BookDto> getBookByIsbn(@RequestParam String isbn) {
        Optional<BookDto> book = bookService.getBookByIsbn(isbn);
        return book.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound()
                        .build());
    }

    /**
     * Update book details
     */
    @PutMapping("/update")
    public ResponseEntity<BookDto> updateBook(@RequestParam Long id, @RequestBody BookUpdateDto bookUpdateDto) {
        try {
            BookDto updatedBook = bookService.updateBook(id, bookUpdateDto);
            return ResponseEntity.ok(updatedBook);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound()
                    .build();
        }
    }

    /**
     * Delete book by ID
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteBook(@RequestParam Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.noContent()
                    .build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .build();
        }
    }

    /**
     * Search books by keyword
     */
    @GetMapping("/search")
    public ResponseEntity<List<BookDto>> searchBooks(@RequestParam String keyword) {
        List<BookDto> books = bookService.searchBooks(keyword);
        return ResponseEntity.ok(books);
    }

    /**
     * Check if book exists by ISBN
     */
    @GetMapping("/exists/isbn")
    public ResponseEntity<Boolean> existsByIsbn(@RequestParam String isbn) {
        boolean exists = bookService.existsByIsbn(isbn);
        return ResponseEntity.ok(exists);
    }

    /**
     * Get book copies for a specific book
     */
    @GetMapping("/copies")
    public ResponseEntity<List<BookCopy>> getBookCopies(@RequestParam Long bookId) {
        try {
            List<BookCopy> copies = bookService.getBookCopies(bookId);
            return ResponseEntity.ok(copies);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound()
                    .build();
        }
    }
} 
