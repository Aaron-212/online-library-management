package com.aaron212.onlinelibrarymanagement.backend.service;

import com.aaron212.onlinelibrarymanagement.backend.dto.BookCreateDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BookSummaryDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BookUpdateDto;
import com.aaron212.onlinelibrarymanagement.backend.exception.BusinessLogicException;
import com.aaron212.onlinelibrarymanagement.backend.exception.DuplicateResourceException;
import com.aaron212.onlinelibrarymanagement.backend.exception.ResourceNotFoundException;
import com.aaron212.onlinelibrarymanagement.backend.model.*;
import com.aaron212.onlinelibrarymanagement.backend.repository.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final IndexCategoryRepository indexCategoryRepository;
    private final IndexCategoryService indexCategoryService;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final BookAuthorRepository bookAuthorRepository;
    private final BookPublisherRepository bookPublisherRepository;

    public BookService(
            BookRepository bookRepository,
            BookCopyRepository bookCopyRepository,
            IndexCategoryRepository indexCategoryRepository,
            IndexCategoryService indexCategoryService,
            AuthorRepository authorRepository,
            PublisherRepository publisherRepository,
            BookAuthorRepository bookAuthorRepository,
            BookPublisherRepository bookPublisherRepository) {
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.indexCategoryRepository = indexCategoryRepository;
        this.indexCategoryService = indexCategoryService;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
        this.bookAuthorRepository = bookAuthorRepository;
        this.bookPublisherRepository = bookPublisherRepository;
    }

    public void createBook(BookCreateDto bookCreateDto) {
        // Check if book with ISBN already exists
        if (bookRepository.existsByIsbn(bookCreateDto.isbn())) {
            throw new DuplicateResourceException("Book", "ISBN", bookCreateDto.isbn());
        }

        // Find or create category
        IndexCategory category = indexCategoryService.addCategoryWithHierarchy(bookCreateDto.categoryName());

        // Create the book
        Book book = new Book();
        book.setIsbn(bookCreateDto.isbn());
        book.setTitle(bookCreateDto.title());
        book.setLanguage(bookCreateDto.language());
        book.setDescription(bookCreateDto.description());
        book.setIndexCategory(category);
        book.setLocation("LIBRARY"); // Default location since frontend doesn't provide it

        Book savedBook = bookRepository.save(book);

        // Handle authors
        if (bookCreateDto.authorNames() != null && !bookCreateDto.authorNames().isEmpty()) {
            for (String authorName : bookCreateDto.authorNames()) {
                if (authorName != null && !authorName.trim().isEmpty()) {
                    Author author = authorRepository.findByName(authorName.trim())
                            .orElseGet(() -> {
                                Author newAuthor = new Author();
                                newAuthor.setName(authorName.trim());
                                return authorRepository.save(newAuthor);
                            });

                    // Create book-author relationship if it doesn't exist
                    if (!bookAuthorRepository.existsByBookAndAuthor(savedBook, author)) {
                        BookAuthor bookAuthor = new BookAuthor();
                        bookAuthor.setBook(savedBook);
                        bookAuthor.setAuthor(author);
                        bookAuthorRepository.save(bookAuthor);
                    }
                }
            }
        }

        // Handle publishers
        if (bookCreateDto.publisherNames() != null && !bookCreateDto.publisherNames().isEmpty()) {
            for (String publisherName : bookCreateDto.publisherNames()) {
                if (publisherName != null && !publisherName.trim().isEmpty()) {
                    Publisher publisher = publisherRepository.findByName(publisherName.trim())
                            .orElseGet(() -> {
                                Publisher newPublisher = new Publisher();
                                newPublisher.setName(publisherName.trim());
                                return publisherRepository.save(newPublisher);
                            });

                    // Create book-publisher relationship if it doesn't exist
                    if (!bookPublisherRepository.existsByBookAndPublisher(savedBook, publisher)) {
                        BookPublisher bookPublisher = new BookPublisher();
                        bookPublisher.setBook(savedBook);
                        bookPublisher.setPublisher(publisher);
                        bookPublisherRepository.save(bookPublisher);
                    }
                }
            }
        }

        // Create book copies
        for (int i = 0; i < bookCreateDto.totalQuantity(); i++) {
            BookCopy copy = new BookCopy();
            copy.setBook(savedBook);
            copy.setBarcode(generateBarcode(savedBook.getIsbn(), i + 1));
            copy.setStatus(BookCopy.Status.AVAILABLE);
            bookCopyRepository.save(copy);
        }
    }

    private String generateBarcode(String isbn, int copyNumber) {
        // Generate a unique barcode based on ISBN and copy number
        return isbn + "-" + String.format("%03d", copyNumber);
    }

    @Transactional(readOnly = true)
    public Page<Book> getAllBooksPaged(Pageable pageable) {
        // Use standard findAll for pagination
        return bookRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<BookSummaryDto> getAllBooksSummaryPaged(Pageable pageable) {
        // First, fetch books with authors
        Page<Book> booksWithAuthors = bookRepository.findAllWithAuthors(pageable);
        
        // Extract book IDs
        List<Long> bookIds = booksWithAuthors.getContent().stream()
                .map(Book::getId)
                .toList();
        
        // Fetch the same books with publishers
        List<Book> booksWithPublishers = bookRepository.findBooksWithPublishersByIds(bookIds);
        
        // Create a map for quick lookup of publishers by book ID
        Map<Long, List<String>> publishersMap = booksWithPublishers.stream()
                .collect(Collectors.toMap(
                    Book::getId,
                    book -> book.getPublishers() != null 
                        ? book.getPublishers().stream()
                            .map(bp -> bp.getPublisher().getName())
                            .toList()
                        : List.of()
                ));
        
        return booksWithAuthors.map(book -> convertToBookSummaryDto(book, publishersMap));
    }

    private BookSummaryDto convertToBookSummaryDto(Book book, Map<Long, List<String>> publishersMap) {
        List<String> authorNames = book.getAuthors() != null 
            ? book.getAuthors().stream()
                .map(bookAuthor -> bookAuthor.getAuthor().getName())
                .toList()
            : List.of();

        List<String> publisherNames = publishersMap != null
            ? publishersMap.getOrDefault(book.getId(), List.of())
            : book.getPublishers() != null
                ? book.getPublishers().stream()
                    .map(bp -> bp.getPublisher().getName())
                    .toList()
                : List.of();

        return new BookSummaryDto(
            book.getId(),
            book.getTitle(),
            authorNames,
            publisherNames,
            book.getCoverURL()
        );
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
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        IndexCategory category = indexCategoryService.addCategoryWithHierarchy(bookUpdateDto.indexCategory());

        book.setTitle(bookUpdateDto.title());
        book.setIndexCategory(category);
        book.setLocation(bookUpdateDto.location());

        bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        Book book = bookRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        // Check if there are any book copies
        List<BookCopy> copies = bookCopyRepository.findByBook(book);
        if (!copies.isEmpty()) {
            throw new BusinessLogicException("Cannot delete book with existing copies. Please remove all copies first.");
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
                .orElseThrow(() -> new ResourceNotFoundException("IndexCategory", "indexCode", categoryCode));

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
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));

        return bookCopyRepository.findByBook(book);
    }

    public int getAvailableCopiesCount(Book book) {
        return (int) bookCopyRepository.countByBookAndStatus(book, BookCopy.Status.AVAILABLE);
    }

    @Transactional(readOnly = true)
    public int getTotalCopiesCount(Book book) {
        return (int) bookCopyRepository.countByBook(book);
    }

    public Optional<Book> findByIsbn(@NotBlank String isbn) {
        return bookRepository.findByIsbn(isbn);
    }
}
