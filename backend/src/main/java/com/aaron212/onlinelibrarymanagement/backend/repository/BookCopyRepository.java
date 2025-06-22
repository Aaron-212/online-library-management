package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.model.BookCopy;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    List<BookCopy> findByBook(Book book);

    /* Counts book copies by their current status */
    long countByStatus(BookCopy.Status status);

    long countByBook(Book book);

    long countByBookAndStatus(Book book, BookCopy.Status status);

    /* Find by barcode - useful for uniqueness checks */
    Optional<BookCopy> findByBarcode(String barcode);

    /* Find copies by status */
    List<BookCopy> findByStatus(BookCopy.Status status);

    /* Find first available copy for a book */
    Optional<BookCopy> findFirstByBookAndStatus(Book book, BookCopy.Status status);
}
