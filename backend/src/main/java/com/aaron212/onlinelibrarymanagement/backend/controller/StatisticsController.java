package com.aaron212.onlinelibrarymanagement.backend.controller;
import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/top-borrowed-books")
    public ResponseEntity<List<Map.Entry<Book, Long>>> getTopBorrowedBooks(@RequestParam(defaultValue = "10") int topCount) {
        List<Map.Entry<Book, Long>> topBooks = statisticsService.getTopBorrowedBooks(topCount);
        return ResponseEntity.ok(topBooks);
    }
    @GetMapping("/borrow-trend/week")
    public ResponseEntity<Map<String, Long>> getBorrowTrendByWeek() {
        Map<String, Long> trend = statisticsService.getBorrowTrendByWeek();
        return ResponseEntity.ok(trend);
    }

    @GetMapping("/borrow-trend/month")
    public ResponseEntity<Map<String, Long>> getBorrowTrendByMonth() {
        Map<String, Long> trend = statisticsService.getBorrowTrendByMonth();
        return ResponseEntity.ok(trend);
    }
    @GetMapping("/book-inventory")
    public ResponseEntity<Map<?, Map<String, Integer>>> getBookInventoryStatistics() {
        Map<?, Map<String, Integer>> statistics = statisticsService.getBookInventoryStatistics();
        return ResponseEntity.ok(statistics);
    }
    @GetMapping("/user-behavior")
    public ResponseEntity<Map<String, Long>> getUserBehaviorAnalysis() {
        Map<String, Long> analysis = statisticsService.getUserBehaviorAnalysis();
        return ResponseEntity.ok(analysis);
    }
}