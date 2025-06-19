package com.aaron212.onlinelibrarymanagement.backend.service;

import com.aaron212.onlinelibrarymanagement.backend.model.BookCopy;
import com.aaron212.onlinelibrarymanagement.backend.model.Borrow;
import com.aaron212.onlinelibrarymanagement.backend.repository.BookCopyRepository;
import com.aaron212.onlinelibrarymanagement.backend.repository.BorrowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class FeeService {

    private final BorrowRepository borrowRepository;
    private final BookCopyRepository bookCopyRepository;

    public FeeService(BorrowRepository borrowRepository, BookCopyRepository bookCopyRepository) {
        this.borrowRepository = borrowRepository;
        this.bookCopyRepository = bookCopyRepository;
    }

    // -------------------- 逾期罚款计算 --------------------
    @Transactional
    public Borrow calculateOverdueFine(Long borrowId) {
        Optional<Borrow> borrowOpt = borrowRepository.findById(borrowId);
        if (!borrowOpt.isPresent()) {
            return null;
        }

        Borrow borrow = borrowOpt.get();

        // 修正：使用 BigDecimal 的 compareTo 方法
        if (borrow.getActualReturnTime() != null || borrow.getFine().compareTo(BigDecimal.ZERO) > 0) {
            return borrow;
        }

        LocalDateTime dueDate = borrow.getReturnTime();
        LocalDateTime now = LocalDateTime.now();

        // 计算逾期天数
        long daysOverdue = ChronoUnit.DAYS.between(dueDate, now);
        if (daysOverdue <= 0) {
            borrow.setStatus(Borrow.Status.BORROWED);
            borrow.setFine(BigDecimal.ZERO);
            return borrowRepository.save(borrow);
        }

        // 罚款规则：每天1元
        BigDecimal finePerDay = BigDecimal.valueOf(1.0);
        BigDecimal totalFine = BigDecimal.valueOf(daysOverdue).multiply(finePerDay);

        // 更新借阅记录
        borrow.setFine(totalFine);
        borrow.setStatus(Borrow.Status.OVERDUE);
        return borrowRepository.save(borrow);
    }

    // -------------------- 赔书费用计算 --------------------
    @Transactional
    public Borrow calculateBookCompensation(Long borrowId) {
        Optional<Borrow> borrowOpt = borrowRepository.findById(borrowId);
        if (!borrowOpt.isPresent()) {
            return null;
        }

        Borrow borrow = borrowOpt.get();

        // 获取图书副本信息
        Optional<BookCopy> copyOpt = bookCopyRepository.findById(borrow.getCopy().getId());
        if (!copyOpt.isPresent()) {
            return borrow;
        }

        BookCopy copy = copyOpt.get();

        // 赔书费用规则：按副本价格的100%计算
        BigDecimal compensation = copy.getPurchasePrice();

        // 更新借阅记录
        borrow.setFine(compensation);
        borrow.setStatus(Borrow.Status.LOST);
        borrow.setActualReturnTime(LocalDateTime.now());

        // 更新副本状态为已报废
        copy.setStatus(BookCopy.Status.DISCARDED); // 使用枚举替代硬编码
        bookCopyRepository.save(copy);

        return borrowRepository.save(borrow);
    }

    // -------------------- 查询用户待缴费用 --------------------
    public List<Borrow> getUnpaidFeesByUser(Long userId) {
        return borrowRepository.findUnpaidFeesByUser(userId);
    }
}
