package com.aaron212.onlinelibrarymanagement.backend.service;

import com.aaron212.onlinelibrarymanagement.backend.exception.BusinessLogicException;
import com.aaron212.onlinelibrarymanagement.backend.exception.ResourceNotFoundException;
import com.aaron212.onlinelibrarymanagement.backend.model.BookCopy;
import com.aaron212.onlinelibrarymanagement.backend.model.Borrow;
import com.aaron212.onlinelibrarymanagement.backend.model.User;
import com.aaron212.onlinelibrarymanagement.backend.repository.BookCopyRepository;
import com.aaron212.onlinelibrarymanagement.backend.repository.BorrowRepository;
import com.aaron212.onlinelibrarymanagement.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final BorrowingRuleService borrowingRuleService;
    private static final Logger logger = LoggerFactory.getLogger(BorrowService.class);

    public BorrowService(BorrowRepository borrowRepository, BookCopyRepository bookCopyRepository, 
                        UserRepository userRepository, BorrowingRuleService borrowingRuleService) {
        this.borrowRepository = borrowRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.userRepository = userRepository;
        this.borrowingRuleService = borrowingRuleService;
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
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        BookCopy copy = bookCopyRepository.findById(copyId)
                .orElseThrow(() -> new ResourceNotFoundException("BookCopy", "id", copyId));

        if (copy.getStatus() != BookCopy.Status.AVAILABLE) {
            throw new BusinessLogicException("图书不可借");
        }

        // Check maximum borrowing limit
        List<Borrow> currentBorrowings = getCurrentBorrowings(userId);
        Integer maxBorrowBooks = borrowingRuleService.getIntegerRule(BorrowingRuleService.MAX_BORROW_BOOKS);
        if (currentBorrowings.size() >= maxBorrowBooks) {
            throw new BusinessLogicException("您已达到最大借阅数量限制（" + maxBorrowBooks + "本）");
        }

        // Check if user already borrowed this book
        List<Borrow> existingBorrows = borrowRepository.findByBookId(copy.getBook().getId());
        boolean userAlreadyBorrowed = existingBorrows.stream()
                .anyMatch(b -> b.getUser().getId().equals(userId) && b.getStatus() == Borrow.Status.BORROWED);

        if (userAlreadyBorrowed) {
            throw new BusinessLogicException("您已借阅此书，不能重复借阅");
        }

        // Create borrow record
        Borrow borrow = new Borrow();
        borrow.setUser(user);
        borrow.setCopy(copy);
        borrow.setBorrowTime(LocalDateTime.now());
        
        // Use configurable loan period
        Integer loanPeriodDays = borrowingRuleService.getIntegerRule(BorrowingRuleService.LOAN_PERIOD_DAYS);
        borrow.setReturnTime(LocalDateTime.now().plusDays(loanPeriodDays));
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
                .orElseThrow(() -> new ResourceNotFoundException("Active borrow record for user " + userId + " and copy " + copyId));

        LocalDateTime now = LocalDateTime.now();
        boolean overdue = now.isAfter(borrow.getReturnTime());

        // Update borrow record
        borrow.setActualReturnTime(now);
        borrow.setStatus(overdue ? Borrow.Status.OVERDUE : Borrow.Status.RETURNED);

        // Calculate fine if overdue
        if (overdue) {
            long overdueDays = java.time.Duration.between(borrow.getReturnTime(), now).toDays();
            BigDecimal finePerDay = borrowingRuleService.getDecimalRule(BorrowingRuleService.FINE_PER_DAY);
            BigDecimal fine = finePerDay.multiply(BigDecimal.valueOf(overdueDays));
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
        // Check if renewals are allowed
        Boolean allowRenewals = borrowingRuleService.getBooleanRule(BorrowingRuleService.ALLOW_RENEWALS);
        if (!allowRenewals) {
            throw new BusinessLogicException("系统不允许续借");
        }

        Borrow borrow = findActiveBorrow(userId, copyId)
                .orElseThrow(() -> new ResourceNotFoundException("Active borrow record for user " + userId + " and copy " + copyId));
        
        // Check if book is overdue
        if (LocalDateTime.now().isAfter(borrow.getReturnTime())) {
            throw new BusinessLogicException("图书已逾期，无法续借");
        }

        // TODO: Check for reservations
        // This would require a Reservation entity and service
        // if (hasReservations(copyId)) {
        //     throw new RuntimeException("图书已被预约，无法续借");
        // }

        // TODO: Check renewal count limit (requires adding renewalCount field to Borrow entity)
        // Integer maxRenewalTimes = borrowingRuleService.getIntegerRule(BorrowingRuleService.MAX_RENEWAL_TIMES);
        // if (borrow.getRenewalCount() >= maxRenewalTimes) {
        //     throw new BusinessLogicException("已达到最大续借次数限制（" + maxRenewalTimes + "次）");
        // }

        // Extend return time by renewal period
        Integer renewalPeriodDays = borrowingRuleService.getIntegerRule(BorrowingRuleService.RENEWAL_PERIOD_DAYS);
        borrow.setReturnTime(borrow.getReturnTime().plusDays(renewalPeriodDays));
        
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
     * Check if user can borrow more books
     * @param userId User ID
     * @return true if user can borrow more books
     */
    public boolean canBorrowMoreBooks(Long userId) {
        List<Borrow> currentBorrowings = getCurrentBorrowings(userId);
        Integer maxBorrowBooks = borrowingRuleService.getIntegerRule(BorrowingRuleService.MAX_BORROW_BOOKS);
        return currentBorrowings.size() < maxBorrowBooks;
    }

    /**
     * Get remaining borrowing capacity for a user
     * @param userId User ID
     * @return Number of books user can still borrow
     */
    public int getRemainingBorrowingCapacity(Long userId) {
        List<Borrow> currentBorrowings = getCurrentBorrowings(userId);
        Integer maxBorrowBooks = borrowingRuleService.getIntegerRule(BorrowingRuleService.MAX_BORROW_BOOKS);
        return Math.max(0, maxBorrowBooks - currentBorrowings.size());
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
        throw new BusinessLogicException("预约功能暂未实现");
    }

    /**
     * Send notification (placeholder for future implementation)
     */
    private void sendNotification(Long userId, String message) {
        // TODO: Implement notification service
        logger.info("向用户 {} 发送通知：{}", userId, message);
    }

    public List<Borrow> getAllBorrowings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return borrowRepository.findAll(pageable).getContent();
    }
}
