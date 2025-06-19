package com.aaron212.onlinelibrarymanagement.backend.service;

import com.aaron212.onlinelibrarymanagement.backend.dto.BookCreateDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BookDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BookUpdateDto;
import com.aaron212.onlinelibrarymanagement.backend.mapper.BookMapper;
import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.model.BookCopy;
import com.aaron212.onlinelibrarymanagement.backend.model.BookLocation;
import com.aaron212.onlinelibrarymanagement.backend.model.IndexCategory;
import com.aaron212.onlinelibrarymanagement.backend.repository.BookCopyRepository;
import com.aaron212.onlinelibrarymanagement.backend.repository.BookLocationRepository;
import com.aaron212.onlinelibrarymanagement.backend.repository.BookRepository;
import com.aaron212.onlinelibrarymanagement.backend.repository.IndexCategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final IndexCategoryRepository indexCategoryRepository;
    private final BookLocationRepository bookLocationRepository;

    public BookService(
            BookRepository bookRepository,
            BookCopyRepository bookCopyRepository,
            IndexCategoryRepository indexCategoryRepository,
            BookLocationRepository bookLocationRepository) {
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.indexCategoryRepository = indexCategoryRepository;
        this.bookLocationRepository = bookLocationRepository;
    }

    /**
     * Create a new book
     */
    public BookDto createBook(BookCreateDto bookCreateDto) {
        // Check if book with ISBN already exists
        if (bookRepository.existsByIsbn(bookCreateDto.isbn())) {
            throw new RuntimeException("Book with ISBN " + bookCreateDto.isbn() + " already exists");
        }

        IndexCategory category = indexCategoryRepository
                .findById(bookCreateDto.indexCategoryId())
                .orElseThrow(() -> new RuntimeException("Index category not found"));

        BookLocation location = bookLocationRepository
                .findById(bookCreateDto.locationId())
                .orElseThrow(() -> new RuntimeException("Book location not found"));

        Book book = BookMapper.INSTANCE.bookCreateDtoToBook(bookCreateDto);

        Book savedBook = bookRepository.save(book);
        return BookMapper.INSTANCE.bookToBookDto(savedBook);
    }

    /**
     * Get all books with pagination
     */
    @Transactional(readOnly = true)
    public Page<BookDto> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable).map(BookMapper.INSTANCE::bookToBookDto);
    }

    /**
     * Get book by ID
     */
    @Transactional(readOnly = true)
    public Optional<BookDto> getBookById(Long id) {
        return bookRepository.findById(id).map(BookMapper.INSTANCE::bookToBookDto);
    }

    /**
     * Get book by ISBN
     */
    @Transactional(readOnly = true)
    public Optional<BookDto> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn).map(BookMapper.INSTANCE::bookToBookDto);
    }

    /**
     * Update book details
     */
    public BookDto updateBook(Long id, BookUpdateDto bookUpdateDto) {
        Book book =
                bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        IndexCategory category = indexCategoryRepository
                .findById(bookUpdateDto.indexCategoryId())
                .orElseThrow(() -> new RuntimeException("Index category not found"));

        BookLocation location = bookLocationRepository
                .findById(bookUpdateDto.locationId())
                .orElseThrow(() -> new RuntimeException("Book location not found"));

        book.setTitle(bookUpdateDto.title());
        book.setIndexCategory(category);
        book.setLocation(location);

        Book updatedBook = bookRepository.save(book);
        return BookMapper.INSTANCE.bookToBookDto(updatedBook);
    }

    /**
     * Delete book by ID
     */
    public void deleteBook(Long id) {
        Book book =
                bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        // Check if there are any book copies
        List<BookCopy> copies = bookCopyRepository.findByBook(book);
        if (!copies.isEmpty()) {
            throw new RuntimeException("Cannot delete book with existing copies. Please remove all copies first.");
        }

        bookRepository.delete(book);
    }

    /**
     * Search books by keyword
     */
    @Transactional(readOnly = true)
    public List<BookDto> searchBooks(String keyword) {
        return bookRepository.searchByKeyword(keyword).stream()
                .map(BookMapper.INSTANCE::bookToBookDto)
                .toList();
    }

    /**
     * Get books by category
     */
    @Transactional(readOnly = true)
    public List<BookDto> getBooksByCategory(Long categoryId) {
        IndexCategory category = indexCategoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Index category not found"));

        return bookRepository.findByIndexCategory(category).stream()
                .map(BookMapper.INSTANCE::bookToBookDto)
                .toList();
    }

    /**
     * Get books by location
     */
    @Transactional(readOnly = true)
    public List<BookDto> getBooksByLocation(Long locationId) {
        BookLocation location = bookLocationRepository
                .findById(locationId)
                .orElseThrow(() -> new RuntimeException("Book location not found"));

        return bookRepository.findByLocation(location).stream()
                .map(BookMapper.INSTANCE::bookToBookDto)
                .toList();
    }

    /**
     * Check if book exists by ISBN
     */
    @Transactional(readOnly = true)
    public boolean existsByIsbn(String isbn) {
        return bookRepository.existsByIsbn(isbn);
    }

    /**
     * Get book copies for a specific book
     */
    @Transactional(readOnly = true)
    public List<BookCopy> getBookCopies(Long bookId) {
        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        return bookCopyRepository.findByBook(book);
    }

    /**
     * Get available copies count for a book
     */
    @Transactional(readOnly = true)
    public int getAvailableCopiesCount(Book book) {
        return (int) bookCopyRepository.findByBook(book).stream()
                .filter(copy -> copy.getStatus() == BookCopy.Status.AVAILABLE)
                .count();
    }

    /**
     * Get total copies count for a book
     */
    @Transactional(readOnly = true)
    public int getTotalCopiesCount(Book book) {
        return bookCopyRepository.findByBook(book).size();
    }
}
