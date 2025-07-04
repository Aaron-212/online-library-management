package com.aaron212.onlinelibrarymanagement.backend.service;

import com.aaron212.onlinelibrarymanagement.backend.dto.BookStatisticsDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.LibraryStatisticsDto;
import com.aaron212.onlinelibrarymanagement.backend.mapper.BookMapper;
import com.aaron212.onlinelibrarymanagement.backend.model.*;
import com.aaron212.onlinelibrarymanagement.backend.repository.*;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final IndexCategoryRepository indexCategoryRepository;
    private final UserRepository userRepository;

    public StatisticsService(
            BorrowRepository borrowRepository,
            BookRepository bookRepository,
            BookCopyRepository bookCopyRepository,
            IndexCategoryRepository indexCategoryRepository,
            UserRepository userRepository) {
        this.borrowRepository = borrowRepository;
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.indexCategoryRepository = indexCategoryRepository;
        this.userRepository = userRepository;
    }

    public List<BookStatisticsDto> getTopBorrowedBooks(int topCount) {
        List<Borrow> allBorrows = borrowRepository.findAll();
        return allBorrows.stream()
                .collect(Collectors.groupingBy(borrow -> borrow.getCopy().getBook(), Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<Book, Long>comparingByValue().reversed())
                .limit(topCount)
                .map(entry -> BookMapper.INSTANCE.toBookStatisticsDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public Map<String, Long> getBorrowTrendByMonth() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());

        List<Borrow> borrows =
                borrowRepository.findByBorrowTimeBetween(startOfMonth.atStartOfDay(), endOfMonth.atTime(23, 59, 59));
        Map<String, Long> trend = new HashMap<>();
        trend.put("borrowCount", (long) borrows.size());
        trend.put(
                "userActivity", borrows.stream().map(Borrow::getUser).distinct().count());
        return trend;
    }

    public Map<String, Long> getBorrowTrendByWeek() {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        List<Borrow> borrows =
                borrowRepository.findByBorrowTimeBetween(startOfWeek.atStartOfDay(), endOfWeek.atTime(23, 59, 59));
        Map<String, Long> trend = new HashMap<>();
        trend.put("borrowCount", (long) borrows.size());
        trend.put(
                "userActivity", borrows.stream().map(Borrow::getUser).distinct().count());
        return trend;
    }

    public Map<IndexCategory, Map<String, Long>> getBookInventoryStatistics() {
        List<IndexCategory> categories = indexCategoryRepository.findAll();
        Map<IndexCategory, Map<String, Long>> statistics = new HashMap<>();

        for (IndexCategory category : categories) {
            List<Book> booksInCategory = bookRepository.findByIndexCategory(category);
            long totalCount = 0;
            long availableCount = 0;

            for (Book book : booksInCategory) {
                List<BookCopy> copies = bookCopyRepository.findByBook(book);
                totalCount += copies.size();
                availableCount += (int) copies.stream()
                        .filter(copy -> copy.getStatus() == BookCopy.Status.AVAILABLE)
                        .count();
            }

            Map<String, Long> categoryStats = new HashMap<>();
            categoryStats.put("totalCount", totalCount);
            categoryStats.put("availableCount", availableCount);
            statistics.put(category, categoryStats);
        }

        return statistics;
    }

    public Map<String, Long> getUserBehaviorAnalysis() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());

        List<User> allUsers = userRepository.findAll();
        long totalUserCount = allUsers.size();
        long registrationCount = allUsers.stream()
                .filter(user ->
                        user.getCreatedTime().toLocalDateTime().toLocalDate().isAfter(startOfMonth))
                .count();

        long activeUserCount = borrowRepository.findAll().stream()
                .map(Borrow::getUser)
                .distinct()
                .count();

        Map<String, Long> analysis = new HashMap<>();
        analysis.put("totalUserCount", totalUserCount);
        analysis.put("registrationCount", registrationCount);
        analysis.put("activeUserCount", activeUserCount);
        return analysis;
    }

    /**
     * Returns high-level aggregated statistics about the library collection and borrowing activity. The
     * returned object is shaped to match the fields expected by the front-end `BookStatisticsDto` TS
     * interface so that no changes are required on the UI side.
     */
    public LibraryStatisticsDto getLibraryStatistics() {
        long totalBooks = bookCopyRepository.count();

        long availableBooks = bookCopyRepository.countByStatus(BookCopy.Status.AVAILABLE);
        long borrowedBooks = bookCopyRepository.countByStatus(BookCopy.Status.BORROWED);

        long totalBorrows = borrowRepository.count();
        long activeBorrows = borrowRepository.countByStatus(Borrow.Status.BORROWED);
        long overdueBorrows = borrowRepository.countByStatus(Borrow.Status.OVERDUE);

        return new LibraryStatisticsDto(
                totalBooks, availableBooks, borrowedBooks, totalBorrows, activeBorrows, overdueBorrows);
    }
}
