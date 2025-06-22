// BookRepository.java
package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.model.IndexCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.isbn LIKE %:keyword%")
    Page<Book> pagedSearchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Book> findByIndexCategory(IndexCategory category, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.indexCategory = :category")
    java.util.List<Book> findByIndexCategory(@Param("category") IndexCategory category);

    @Query("SELECT DISTINCT b FROM Book b " + "LEFT JOIN FETCH b.authors ba " + "LEFT JOIN FETCH ba.author")
    Page<Book> findAllWithAuthors(Pageable pageable);

    @Query("SELECT DISTINCT b FROM Book b " + "LEFT JOIN FETCH b.publishers bp "
            + "LEFT JOIN FETCH bp.publisher "
            + "WHERE b.id IN :bookIds")
    List<Book> findBooksWithPublishersByIds(@Param("bookIds") List<Long> bookIds);
}
