package com.aaron212.onlinelibrarymanagement.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "book_copy")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Barcode is required")
    private String barcode;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, columnDefinition = "TINYINT")
    @NotNull(message = "Status is required")
    private Status status;

    @Column(precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Column
    private LocalDateTime purchaseTime;

    @Column
    private LocalDateTime lastMaintenance;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateTime;

    @Getter
    public enum Status {
        AVAILABLE(1),
        BORROWED(2),
        MAINTENANCE(3),
        SCRAPPED(4),
        DISCARDED(4);

        private final int value;

        Status(int value) {
            this.value = value;
        }
    }
}
