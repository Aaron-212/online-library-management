// BookRepository.java
package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.model.IndexCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);

    Page<Book> pagedFindAll(Pageable pageable);

    boolean existsByIsbn(String isbn);

    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.isbn LIKE %:keyword%")
    Page<Book> pagedSearchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Book> pagedFindByIndexCategory(IndexCategory category, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.indexCategory = :category")
    java.util.List<Book> findByIndexCategory(@Param("category") IndexCategory category);
}
