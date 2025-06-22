package com.aaron212.onlinelibrarymanagement.backend.service;

import com.aaron212.onlinelibrarymanagement.backend.model.BookCopy;
import com.aaron212.onlinelibrarymanagement.backend.model.Borrow;
import com.aaron212.onlinelibrarymanagement.backend.repository.BookCopyRepository;
import com.aaron212.onlinelibrarymanagement.backend.repository.BorrowRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeeService {

    private final BorrowRepository borrowRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BorrowingRuleService borrowingRuleService;

    public FeeService(
            BorrowRepository borrowRepository,
            BookCopyRepository bookCopyRepository,
            BorrowingRuleService borrowingRuleService) {
        this.borrowRepository = borrowRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.borrowingRuleService = borrowingRuleService;
    }

    // -------------------- 逾期罚款计算 --------------------
    @Transactional
    public Borrow calculateOverdueFine(Long borrowId) {
        Borrow borrow = borrowRepository
                .findById(borrowId)
                .orElseThrow(() -> new com.aaron212.onlinelibrarymanagement.backend.exception.ResourceNotFoundException(
                        "Borrow", "id", borrowId));

        // 若已经归还或已计算罚款则直接返回
        if (borrow.getActualReturnTime() != null
                || (borrow.getFine() != null && borrow.getFine().compareTo(BigDecimal.ZERO) > 0)) {
            return borrow;
        }

        long daysOverdue = ChronoUnit.DAYS.between(borrow.getReturnTime(), LocalDateTime.now());
        if (daysOverdue <= 0) {
            borrow.setStatus(Borrow.Status.BORROWED);
            borrow.setFine(BigDecimal.ZERO);
            return borrowRepository.save(borrow);
        }

        BigDecimal finePerDay = borrowingRuleService.getDecimalRule(BorrowingRuleService.FINE_PER_DAY);
        BigDecimal totalFine = finePerDay.multiply(BigDecimal.valueOf(daysOverdue));

        borrow.setFine(totalFine);
        borrow.setStatus(Borrow.Status.OVERDUE);
        return borrowRepository.save(borrow);
    }

    // -------------------- 赔书费用计算 --------------------
    @Transactional
    public Borrow calculateBookCompensation(Long borrowId) {
        Borrow borrow = borrowRepository
                .findById(borrowId)
                .orElseThrow(() -> new com.aaron212.onlinelibrarymanagement.backend.exception.ResourceNotFoundException(
                        "Borrow", "id", borrowId));

        BookCopy copy = borrow.getCopy();

        BigDecimal compensation = Optional.ofNullable(copy.getPurchasePrice()).orElse(BigDecimal.ZERO);

        borrow.setFine(compensation);
        borrow.setStatus(Borrow.Status.LOST);
        borrow.setActualReturnTime(LocalDateTime.now());

        copy.setStatus(BookCopy.Status.DISCARDED);
        bookCopyRepository.save(copy);

        return borrowRepository.save(borrow);
    }

    // -------------------- 查询用户待缴费用 --------------------
    public List<Borrow> getUnpaidFeesByUser(Long userId) {
        return borrowRepository.findUnpaidFeesByUser(userId);
    }
}
