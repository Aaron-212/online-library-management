// BookRepository.java
package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.model.BookLocation;
import com.aaron212.onlinelibrarymanagement.backend.model.IndexCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByIndexCategory(IndexCategory category);
    
    List<Book> findByLocation(BookLocation location);
    
    Optional<Book> findByIsbn(String isbn);
    
    boolean existsByIsbn(String isbn);

    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.isbn LIKE %:keyword%")
    List<Book> searchByKeyword(@Param("keyword") String keyword);
}
