package com.aaron212.onlinelibrarymanagement.backend.service;

import com.aaron212.onlinelibrarymanagement.backend.dto.BookCreateDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BookUpdateDto;
import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.model.BookCopy;
import com.aaron212.onlinelibrarymanagement.backend.model.IndexCategory;
import com.aaron212.onlinelibrarymanagement.backend.repository.BookCopyRepository;
import com.aaron212.onlinelibrarymanagement.backend.repository.BookRepository;
import com.aaron212.onlinelibrarymanagement.backend.repository.IndexCategoryRepository;
import jakarta.validation.constraints.NotBlank;
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

    public BookService(
            BookRepository bookRepository,
            BookCopyRepository bookCopyRepository,
            IndexCategoryRepository indexCategoryRepository) {
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.indexCategoryRepository = indexCategoryRepository;
    }

    public void createBook(BookCreateDto bookCreateDto) {
        // Check if book with ISBN already exists
        if (bookRepository.existsByIsbn(bookCreateDto.isbn())) {
            throw new RuntimeException("Book with ISBN " + bookCreateDto.isbn() + " already exists");
        }

        IndexCategory category = handleParseIndexCategory(bookCreateDto.indexCategory());

        Book book = new Book();
        book.setIsbn(bookCreateDto.isbn());
        book.setTitle(bookCreateDto.title());
        book.setIndexCategory(category);
        book.setLocation(bookCreateDto.location());

        bookRepository.save(book);
    }

    @Transactional(readOnly = true)
    public Page<Book> getAllBooksPaged(Pageable pageable) {
        // Use standard findAll for pagination
        return bookRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public void updateBook(Long id, BookUpdateDto bookUpdateDto) {
        Book book = bookRepository
                .findById(id).orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        IndexCategory category = handleParseIndexCategory(bookUpdateDto.indexCategory());

        book.setTitle(bookUpdateDto.title());
        book.setIndexCategory(category);
        book.setLocation(bookUpdateDto.location());

        bookRepository.save(book);
    }

    private IndexCategory handleParseIndexCategory(String indexCode) {
        IndexCategory category;
        if (indexCategoryRepository.existsByIndexCode(indexCode)) {
            // Find existing category by ID
            category = indexCategoryRepository
                    .findByIndexCode(indexCode)
                    .orElseThrow(() -> new RuntimeException("Index category not found"));
        } else {
            // Create new category if not provided
            category = new IndexCategory();
            category.setIndexCode(indexCode);
            indexCategoryRepository.save(category);
        }
        return category;
    }

    public void deleteBook(Long id) {
        Book book = bookRepository
                .findById(id).orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        // Check if there are any book copies
        List<BookCopy> copies = bookCopyRepository.findByBook(book);
        if (!copies.isEmpty()) {
            throw new RuntimeException("Cannot delete book with existing copies. Please remove all copies first.");
        }

        bookRepository.delete(book);
    }

    @Transactional(readOnly = true)
    public Page<Book> searchBooksPaged(String keyword, Pageable pageable) {
        return bookRepository.pagedSearchByKeyword(keyword, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Book> getBooksByCategory(String categoryCode, Pageable pageable) {
        IndexCategory category = indexCategoryRepository
                .findByIndexCode(categoryCode)
                .orElseThrow(() -> new RuntimeException("Index category not found"));

        // Use renamed method for paginated find by category
        return bookRepository.findByIndexCategory(category, pageable);
    }

    @Transactional(readOnly = true)
    public boolean existsByIsbn(String isbn) {
        return bookRepository.existsByIsbn(isbn);
    }

    @Transactional(readOnly = true)
    public List<BookCopy> getBookCopies(Long bookId) {
        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        return bookCopyRepository.findByBook(book);
    }

    @Transactional(readOnly = true)
    public int getAvailableCopiesCount(Book book) {
        return (int) bookCopyRepository.findByBook(book).stream()
                .filter(copy -> copy.getStatus() == BookCopy.Status.AVAILABLE)
                .count();
    }

    @Transactional(readOnly = true)
    public int getTotalCopiesCount(Book book) {
        return bookCopyRepository.findByBook(book).size();
    }

    public Optional<Book> findByIsbn(@NotBlank String isbn) {
        return bookRepository.findByIsbn(isbn);
    }
}
