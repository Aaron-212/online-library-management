// BookRepository.java
package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.model.IndexCategory;
import com.aaron212.onlinelibrarymanagement.backend.projection.BookProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByIndexCategory(IndexCategory category);

    Optional<Book> findByIsbn(String isbn);

    Page<BookProjection> findProjectionAllBy(Pageable pageable);

    boolean existsByIsbn(String isbn);

    Optional<BookProjection> findProjectionById(Long id);

    Optional<BookProjection> findProjectionByIsbn(String isbn);

    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.isbn LIKE %:keyword%")
    Page<BookProjection> searchProjectionByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<BookProjection> findProjectionByIndexCategory(IndexCategory category, Pageable pageable);
}
