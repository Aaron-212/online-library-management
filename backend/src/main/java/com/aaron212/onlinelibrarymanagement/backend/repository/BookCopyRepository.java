package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.model.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    List<BookCopy> findByBook(Book book);

    /* Counts book copies by their current status */
    long countByStatus(BookCopy.Status status);

    long countByBook(Book book);

    long countByBookAndStatus(Book book, BookCopy.Status status);
}
