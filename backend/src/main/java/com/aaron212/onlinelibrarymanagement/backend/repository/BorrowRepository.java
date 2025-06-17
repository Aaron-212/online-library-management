// BorrowRepository.java
package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    List<Borrow> findByBorrowTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT b FROM Borrow b WHERE b.copy.book.id = :bookId")
    List<Borrow> findByBookId(@Param("bookId") Long bookId);

    // 查询用户待缴费用（fine > 0 且未支付）
    @Query("SELECT b FROM Borrow b WHERE b.user.id = :userId " + "AND b.fine > 0 "
            + "AND b.actualReturnTime IS NOT NULL "
            + "AND b.status = 3") // 状态：逾期
    List<Borrow> findUnpaidFeesByUser(Long userId);
}
