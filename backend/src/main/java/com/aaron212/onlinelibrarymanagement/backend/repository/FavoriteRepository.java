package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.model.Favorite;
import com.aaron212.onlinelibrarymanagement.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    @Query("SELECT f FROM Favorite f " +
           "LEFT JOIN FETCH f.book b " +
           "LEFT JOIN FETCH b.authors ba " +
           "LEFT JOIN FETCH ba.author " +
           "LEFT JOIN FETCH b.publishers bp " +
           "LEFT JOIN FETCH bp.publisher " +
           "LEFT JOIN FETCH b.indexCategory " +
           "WHERE f.user.id = :userId")
    Page<Favorite> findByUserIdWithBookDetails(@Param("userId") Long userId, Pageable pageable);
    
    boolean existsByUserAndBook(User user, Book book);
    
    Optional<Favorite> findByUserAndBook(User user, Book book);
    
    long countByUserId(Long userId);
    
    void deleteByUserAndBook(User user, Book book);
    
    @Query("SELECT COUNT(f) > 0 FROM Favorite f WHERE f.user.id = :userId AND f.book.id = :bookId")
    boolean existsByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);
}