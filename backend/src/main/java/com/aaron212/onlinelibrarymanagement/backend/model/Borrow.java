package com.aaron212.onlinelibrarymanagement.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrow")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Borrow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "copy_id", nullable = false)
    private BookCopy copy;

    @Column(nullable = false)
    @NotNull(message = "Borrow time is required")
    private LocalDateTime borrowTime;

    @Column(nullable = false)
    @NotNull(message = "Return time is required")
    private LocalDateTime returnTime;

    @Column
    private LocalDateTime actualReturnTime;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, columnDefinition = "TINYINT")
    @NotNull(message = "Status is required")
    private Status status;

    @Column(precision = 10, scale = 2)
    private BigDecimal fine;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateTime;

    @Getter
    public enum Status {
        BORROWING(1), RETURNED(2), OVERDUE(3);

        private final int value;

        Status(int value) {
            this.value = value;
        }
    }
} 
