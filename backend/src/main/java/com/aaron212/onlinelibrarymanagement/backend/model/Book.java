package com.aaron212.onlinelibrarymanagement.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

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

    @NotBlank(message = "Location is required")
    private String location;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Title is required")
    private String title;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    private String coverURL;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private List<BookAuthor> authors;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private List<BookPublisher> publishers;
}
