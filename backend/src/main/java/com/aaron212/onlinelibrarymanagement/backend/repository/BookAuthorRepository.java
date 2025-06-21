package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.model.BookAuthor;
import com.aaron212.onlinelibrarymanagement.backend.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookAuthorRepository extends JpaRepository<BookAuthor, Long> {
    List<BookAuthor> findByBook(Book book);
    List<BookAuthor> findByAuthor(Author author);
    boolean existsByBookAndAuthor(Book book, Author author);
}