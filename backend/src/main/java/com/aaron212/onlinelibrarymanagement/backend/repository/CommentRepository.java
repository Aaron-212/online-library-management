package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.Comment;
import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // Find comments by book with pagination
    Page<Comment> findByBookAndStatus(Book book, Comment.Status status, Pageable pageable);
    
    // Find comments by user with pagination
    Page<Comment> findByUserAndStatus(User user, Comment.Status status, Pageable pageable);
    
    // Find all published comments for a book
    List<Comment> findByBookAndStatusOrderByCreateTimeDesc(Book book, Comment.Status status);
    
    // Find pending comments for moderation
    List<Comment> findByStatusOrderByCreateTimeAsc(Comment.Status status);
    
    // Check if user has already commented on a book
    boolean existsByUserAndBook(User user, Book book);
    
    // Get average rating for a book
    @Query("SELECT AVG(c.rating) FROM Comment c WHERE c.book = :book AND c.status = :status AND c.rating IS NOT NULL")
    Optional<Double> findAverageRatingByBookAndStatus(@Param("book") Book book, @Param("status") Comment.Status status);
    
    // Count comments by book and status
    long countByBookAndStatus(Book book, Comment.Status status);
    
    // Count comments by user and status
    long countByUserAndStatus(User user, Comment.Status status);
}