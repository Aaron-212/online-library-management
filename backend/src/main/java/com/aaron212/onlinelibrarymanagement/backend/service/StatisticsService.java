package com.aaron212.onlinelibrarymanagement.backend.service;

import com.aaron212.onlinelibrarymanagement.backend.model.*;
import com.aaron212.onlinelibrarymanagement.backend.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;

@Service
public class StatisticsService {

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final IndexCategoryRepository indexCategoryRepository;
    private final UserRepository userRepository;

    public StatisticsService(BorrowRepository borrowRepository, BookRepository bookRepository, BookCopyRepository bookCopyRepository, IndexCategoryRepository indexCategoryRepository, UserRepository userRepository) {
        this.borrowRepository = borrowRepository;
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.indexCategoryRepository = indexCategoryRepository;
        this.userRepository = userRepository;
    }

    public List<Map.Entry<Book, Long>> getTopBorrowedBooks(int topCount) {
        List<Borrow> allBorrows = borrowRepository.findAll();
        return allBorrows.stream()
                .collect(Collectors.groupingBy(borrow -> borrow.getCopy().getBook(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Book, Long>comparingByValue().reversed())
                .limit(topCount)
                .collect(Collectors.toList());
    }
    public Map<String, Long> getBorrowTrendByMonth() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());

        List<Borrow> borrows = borrowRepository.findByBorrowTimeBetween(startOfMonth.atStartOfDay(), endOfMonth.atTime(23, 59, 59));
        Map<String, Long> trend = new HashMap<>();
        trend.put("borrowCount", (long) borrows.size());
        trend.put("userActivity", (long) borrows.stream().map(Borrow::getUser).distinct().count());
        return trend;
    }
    public Map<String, Long> getBorrowTrendByWeek() {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        List<Borrow> borrows = borrowRepository.findByBorrowTimeBetween(startOfWeek.atStartOfDay(), endOfWeek.atTime(23, 59, 59));
        Map<String, Long> trend = new HashMap<>();
        trend.put("borrowCount", (long) borrows.size());
        trend.put("userActivity", (long) borrows.stream().map(Borrow::getUser).distinct().count());
        return trend;
    }
    public Map<IndexCategory, Map<String, Integer>> getBookInventoryStatistics() {
        List<IndexCategory> categories = indexCategoryRepository.findAll();
        Map<IndexCategory, Map<String, Integer>> statistics = new HashMap<>();

        for (IndexCategory category : categories) {
            List<Book> booksInCategory = bookRepository.findByIndexCategory(category);
            int totalCount = 0;
            int availableCount = 0;

            for (Book book : booksInCategory) {
                List<BookCopy> copies = bookCopyRepository.findByBook(book);
                totalCount += copies.size();
                availableCount += copies.stream().filter(copy -> copy.getStatus() == BookCopy.Status.AVAILABLE).count();
            }

            Map<String, Integer> categoryStats = new HashMap<>();
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
        long registrationCount = allUsers.stream()
                .filter(user -> user.getCreatedTime().toLocalDateTime().toLocalDate().isAfter(startOfMonth))
                .count();

        long activeUserCount = borrowRepository.findAll().stream()
                .map(Borrow::getUser)
                .distinct()
                .count();

        Map<String, Long> analysis = new HashMap<>();
        analysis.put("registrationCount", registrationCount);
        analysis.put("activeUserCount", activeUserCount);
        return analysis;
    }
}