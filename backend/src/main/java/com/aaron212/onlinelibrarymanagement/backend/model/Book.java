package com.aaron212.onlinelibrarymanagement.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;

@Entity
@Table(name = "book")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    @NotBlank(message = "ISBN is required")
    private String isbn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_category_id", nullable = false)
    private IndexCategory indexCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private BookLocation location;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Title is required")
    private String title;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    private String author;  // 作者
    private static int stock;      // 库存数量

    public Book(Long id, String title, String author, String isbn, int stock) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.stock = stock;
    }

    public static class BorrowRecord {
        public LocalDate getDueDate;
        private Long userId;
        private Long bookId;
        private LocalDate borrowDate;
        private LocalDate dueDate;

        public BorrowRecord(Long userId, Long bookId, LocalDate borrowDate, LocalDate dueDate) {
            this.userId = userId;
            this.bookId = bookId;
            this.borrowDate = borrowDate;
            this.dueDate = dueDate;
        }
        public int getStock() {
            return stock;
        }
        public LocalDate getDueDate() {
            return this.dueDate;
        }

        public Object getUserId() {
            return userId;
        }


    }
    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        // 库存不能为负数
        this.stock = Math.max(stock, 0);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }


}
