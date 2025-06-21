package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.Borrow;
import com.aaron212.onlinelibrarymanagement.backend.projection.BorrowProjection;
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
    @Query("SELECT b FROM Borrow b WHERE b.user.id = :userId " + 
           "AND b.fine > 0 " +
           "AND b.actualReturnTime IS NOT NULL " +
           "AND b.status = 3") // 状态：逾期
    List<Borrow> findUnpaidFeesByUser(@Param("userId") Long userId);
    
    // 查询用户的借阅历史
    @Query("SELECT b FROM Borrow b WHERE b.user.id = :userId ORDER BY b.borrowTime DESC")
    List<Borrow> findByUserIdOrderByBorrowTimeDesc(@Param("userId") Long userId);
    
    // 查询用户当前借阅
    @Query("SELECT b FROM Borrow b WHERE b.user.id = :userId AND b.status = 0 ORDER BY b.borrowTime DESC")
    List<Borrow> findCurrentBorrowingsByUserId(@Param("userId") Long userId);
    
    // 查询所有逾期借阅
    @Query("SELECT b FROM Borrow b WHERE b.status = 0 AND b.returnTime < :currentTime ORDER BY b.returnTime ASC")
    List<Borrow> findOverdueBorrowings(@Param("currentTime") LocalDateTime currentTime);
    
    // 使用投影查询用户借阅历史（优化性能）
    @Query("SELECT b.id as id, b.user.id as userId, b.user.username as username, " +
           "b.copy.id as copyId, b.copy.book.title as bookTitle, b.copy.book.isbn as isbn, " +
           "b.borrowTime as borrowTime, b.returnTime as returnTime, b.actualReturnTime as actualReturnTime, " +
           "b.status as status, b.fine as fine " +
           "FROM Borrow b WHERE b.user.id = :userId ORDER BY b.borrowTime DESC")
    List<BorrowProjection> findBorrowProjectionsByUserId(@Param("userId") Long userId);

    /* Counts borrows by their current status */
    long countByStatus(Borrow.Status status);
}
