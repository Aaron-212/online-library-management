package com.aaron212.onlinelibrarymanagement.backend.service;

import com.aaron212.onlinelibrarymanagement.backend.dto.CommentCreateDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.CommentDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.CommentUpdateDto;
import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.model.Comment;
import com.aaron212.onlinelibrarymanagement.backend.model.User;
import com.aaron212.onlinelibrarymanagement.backend.repository.BookRepository;
import com.aaron212.onlinelibrarymanagement.backend.repository.CommentRepository;
import com.aaron212.onlinelibrarymanagement.backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    // Create a new comment
    public CommentDto createComment(CommentCreateDto commentCreateDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        Book book = bookRepository.findById(commentCreateDto.bookId())
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + commentCreateDto.bookId()));

        // Check if user has already commented on this book
        if (commentRepository.existsByUserIdAndBookId(user.getId(), book.getId())) {
            throw new RuntimeException("User has already commented on this book");
        }

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setBook(book);
        comment.setContent(commentCreateDto.content());
        comment.setRating(commentCreateDto.rating());
        comment.setStatus(Comment.Status.PENDING); // Comments need approval by default

        Comment savedComment = commentRepository.save(comment);
        return convertToDto(savedComment);
    }

    // Get comment by ID
    @Transactional(readOnly = true)
    public Optional<CommentDto> getCommentById(Long id) {
        return commentRepository.findById(id).map(this::convertToDto);
    }

    // Get all published comments for a book with pagination
    @Transactional(readOnly = true)
    public Page<CommentDto> getPublishedCommentsByBook(Long bookId, Pageable pageable) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        
        return commentRepository.findByBookAndStatus(book, Comment.Status.PUBLISHED, pageable)
                .map(this::convertToDto);
    }

    // Get comments by user with pagination
    @Transactional(readOnly = true)
    public Page<CommentDto> getCommentsByUser(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        return commentRepository.findByUserAndStatus(user, Comment.Status.PUBLISHED, pageable)
                .map(this::convertToDto);
    }

    // Get pending comments for moderation (admin only)
    @Transactional(readOnly = true)
    public List<CommentDto> getPendingComments() {
        return commentRepository.findByStatusOrderByCreateTimeAsc(Comment.Status.PENDING)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Update comment (only by the author and only if pending)
    public CommentDto updateComment(Long id, CommentUpdateDto commentUpdateDto, String username) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));

        // Check if user is the author of the comment
        if (!comment.getUser().getUsername().equals(username)) {
            throw new RuntimeException("User is not authorized to update this comment");
        }

        // Only allow updates if comment is still pending
        if (comment.getStatus() != Comment.Status.PENDING) {
            throw new RuntimeException("Only pending comments can be updated");
        }

        comment.setContent(commentUpdateDto.content());
        comment.setRating(commentUpdateDto.rating());

        Comment updatedComment = commentRepository.save(comment);
        return convertToDto(updatedComment);
    }

    // Delete comment (by author or admin)
    public void deleteComment(Long id, String username, User.Role userRole) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));

        // Check if user is authorized to delete (author or admin)
        if (!comment.getUser().getUsername().equals(username) && userRole != User.Role.ADMIN) {
            throw new RuntimeException("User is not authorized to delete this comment");
        }

        comment.setStatus(Comment.Status.DELETED);
        commentRepository.save(comment);
    }

    // Approve comment (admin only)
    public CommentDto approveComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));

        if (comment.getStatus() != Comment.Status.PENDING) {
            throw new RuntimeException("Only pending comments can be approved");
        }

        comment.setStatus(Comment.Status.PUBLISHED);
        Comment approvedComment = commentRepository.save(comment);
        return convertToDto(approvedComment);
    }

    // Reject comment (admin only)
    public void rejectComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));

        if (comment.getStatus() != Comment.Status.PENDING) {
            throw new RuntimeException("Only pending comments can be rejected");
        }

        comment.setStatus(Comment.Status.DELETED);
        commentRepository.save(comment);
    }

    // Get average rating for a book
    @Transactional(readOnly = true)
    public Optional<Double> getAverageRatingForBook(Long bookId) {
        return commentRepository.findAverageRatingByBookIdAndStatus(bookId, Comment.Status.PUBLISHED);
    }

    // Get comment count for a book
    @Transactional(readOnly = true)
    public long getCommentCountForBook(Long bookId) {
        return commentRepository.countByBookIdAndStatus(bookId, Comment.Status.PUBLISHED);
    }

    // Convert Comment entity to DTO
    private CommentDto convertToDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getBook().getId(),
                comment.getBook().getTitle(),
                comment.getContent(),
                comment.getRating(),
                comment.getCreateTime(),
                comment.getStatus()
        );
    }
}