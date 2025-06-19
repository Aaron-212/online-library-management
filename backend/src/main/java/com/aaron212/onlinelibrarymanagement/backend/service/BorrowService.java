package com.aaron212.onlinelibrarymanagement.backend.service;

import com.aaron212.onlinelibrarymanagement.backend.model.BookCopy;
import com.aaron212.onlinelibrarymanagement.backend.model.Borrow;
import com.aaron212.onlinelibrarymanagement.backend.model.User;
import com.aaron212.onlinelibrarymanagement.backend.repository.BookCopyRepository;
import com.aaron212.onlinelibrarymanagement.backend.repository.BorrowRepository;
import com.aaron212.onlinelibrarymanagement.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BorrowService {

    private final BorrowRepository borrowRepository;
    private final BookCopyRepository bookCopyRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(BorrowService.class);

    public BorrowService(BorrowRepository borrowRepository, BookCopyRepository bookCopyRepository, 
                        UserRepository userRepository) {
        this.borrowRepository = borrowRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.userRepository = userRepository;
    }

    /**
     * Borrow a book
     * @param userId User ID
     * @param copyId Book copy ID
     * @return Borrow record
     * @throws RuntimeException if borrowing fails
     */
    public Borrow borrowBook(Long userId, Long copyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        BookCopy copy = bookCopyRepository.findById(copyId)
                .orElseThrow(() -> new RuntimeException("图书副本不存在"));

        if (copy.getStatus() != BookCopy.Status.AVAILABLE) {
            throw new RuntimeException("图书不可借");
        }

        // Check if user already borrowed this book
        List<Borrow> existingBorrows = borrowRepository.findByBookId(copy.getBook().getId());
        boolean userAlreadyBorrowed = existingBorrows.stream()
                .anyMatch(b -> b.getUser().getId().equals(userId) && b.getStatus() == Borrow.Status.BORROWED);

        if (userAlreadyBorrowed) {
            throw new RuntimeException("您已借阅此书，不能重复借阅");
        }

        // Create borrow record
        Borrow borrow = new Borrow();
        borrow.setUser(user);
        borrow.setCopy(copy);
        borrow.setBorrowTime(LocalDateTime.now());
        borrow.setReturnTime(LocalDateTime.now().plusDays(30)); // 30 days loan period
        borrow.setStatus(Borrow.Status.BORROWED);

        // Update copy status
        copy.setStatus(BookCopy.Status.BORROWED);

        borrowRepository.save(borrow);
        bookCopyRepository.save(copy);

        return borrow;
    }

    /**
     * Return a book
     * @param userId User ID
     * @param copyId Book copy ID
     * @return Updated borrow record
     * @throws RuntimeException if return fails
     */
    public Borrow returnBook(Long userId, Long copyId) {
        Borrow borrow = findActiveBorrow(userId, copyId)
                .orElseThrow(() -> new RuntimeException("无借阅记录"));

        LocalDateTime now = LocalDateTime.now();
        boolean overdue = now.isAfter(borrow.getReturnTime());

        // Update borrow record
        borrow.setActualReturnTime(now);
        borrow.setStatus(overdue ? Borrow.Status.OVERDUE : Borrow.Status.RETURNED);

        // Calculate fine if overdue
        if (overdue) {
            long overdueDays = java.time.Duration.between(borrow.getReturnTime(), now).toDays();
            BigDecimal fine = BigDecimal.valueOf(overdueDays * 0.5); // 0.5 yuan per day
            borrow.setFine(fine);
        }

        // Update copy status
        BookCopy copy = borrow.getCopy();
        copy.setStatus(BookCopy.Status.AVAILABLE);

        borrowRepository.save(borrow);
        bookCopyRepository.save(copy);

        // TODO: Handle reservation notifications
        // This would require a Reservation entity and service
        
        return borrow;
    }

    /**
     * Renew a book
     * @param userId User ID
     * @param copyId Book copy ID
     * @return Updated borrow record
     * @throws RuntimeException if renewal fails
     */
    public Borrow renewBook(Long userId, Long copyId) {
        Borrow borrow = findActiveBorrow(userId, copyId)
                .orElseThrow(() -> new RuntimeException("无续借权限"));
        
        // Check if book is overdue
        if (LocalDateTime.now().isAfter(borrow.getReturnTime())) {
            throw new RuntimeException("图书已逾期，无法续借");
        }

        // TODO: Check for reservations
        // This would require a Reservation entity and service
        // if (hasReservations(copyId)) {
        //     throw new RuntimeException("图书已被预约，无法续借");
        // }

        // Extend return time by 15 days
        borrow.setReturnTime(borrow.getReturnTime().plusDays(15));
        return borrowRepository.save(borrow);
    }

    /**
     * Get borrowing history for a user
     * @param userId User ID
     * @return List of borrow records
     */
    public List<Borrow> getBorrowHistory(Long userId) {
        return borrowRepository.findByUserIdOrderByBorrowTimeDesc(userId);
    }

    /**
     * Get current borrowings for a user
     * @param userId User ID
     * @return List of active borrow records
     */
    public List<Borrow> getCurrentBorrowings(Long userId) {
        return borrowRepository.findCurrentBorrowingsByUserId(userId);
    }

    /**
     * Get overdue borrowings
     * @return List of overdue borrow records
     */
    public List<Borrow> getOverdueBorrowings() {
        return borrowRepository.findOverdueBorrowings(LocalDateTime.now());
    }

    /**
     * Find active borrow record for a user and copy
     * @param userId User ID
     * @param copyId Copy ID
     * @return Optional borrow record
     */
    private Optional<Borrow> findActiveBorrow(Long userId, Long copyId) {
        return borrowRepository.findAll().stream()
                .filter(b -> b.getUser().getId().equals(userId) 
                        && b.getCopy().getId().equals(copyId)
                        && b.getStatus() == Borrow.Status.BORROWED)
                .findFirst();
    }

    /**
     * Reserve a book (placeholder for future implementation)
     * This would require a Reservation entity and service
     */
    public void reserveBook(Long userId, Long bookId) {
        // TODO: Implement reservation functionality
        // This would require creating a Reservation entity and ReservationService
        throw new RuntimeException("预约功能暂未实现");
    }

    /**
     * Send notification (placeholder for future implementation)
     */
    private void sendNotification(Long userId, String message) {
        // TODO: Implement notification service
        logger.info("向用户 {} 发送通知：{}", userId, message);
    }
}
