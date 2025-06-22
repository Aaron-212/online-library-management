package com.aaron212.onlinelibrarymanagement.backend.controller;

import com.aaron212.onlinelibrarymanagement.backend.dto.BookStatisticsDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.LibraryStatisticsDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.TopBooksRequestDto;
import com.aaron212.onlinelibrarymanagement.backend.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/statistics")
@Tag(name = "Statistics", description = "Library statistics and analytics endpoints")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Operation(
            summary = "Get top borrowed books",
            description = "Retrieves the most frequently borrowed books",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Top borrowed books retrieved successfully",
                        content = @Content(schema = @Schema(implementation = BookStatisticsDto.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid parameter",
                        content = @Content(schema = @Schema(implementation = Map.class))),
            })
    @PostMapping("/top-borrowed-books")
    public ResponseEntity<?> getTopBorrowedBooks(@Valid @RequestBody TopBooksRequestDto requestDto) {
        try {
            List<BookStatisticsDto> topBooks = statisticsService.getTopBorrowedBooks(requestDto.topCount());
            return ResponseEntity.ok(topBooks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to retrieve top borrowed books"));
        }
    }

    @PostMapping("/top-books")
    public ResponseEntity<?> getTopBooksAlias(@Valid @RequestBody TopBooksRequestDto requestDto) {
        return getTopBorrowedBooks(requestDto);
    }

    @Operation(
            summary = "Get aggregated book statistics",
            description = "Returns high-level statistics about the book collection and borrowing activity",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Statistics retrieved successfully",
                        content = @Content(schema = @Schema(implementation = LibraryStatisticsDto.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = Map.class))),
            })
    @GetMapping("/books")
    public ResponseEntity<?> getBookStatistics() {
        try {
            LibraryStatisticsDto stats = statisticsService.getLibraryStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve book statistics"));
        }
    }

    @Operation(
            summary = "Get weekly borrow trend",
            description = "Retrieves borrowing statistics for the past week",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Weekly borrow trend retrieved successfully",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = Map.class))),
            })
    @GetMapping("/borrow-trends/weekly")
    public ResponseEntity<?> getWeeklyBorrowTrend() {
        try {
            Map<String, Long> trend = statisticsService.getBorrowTrendByWeek();
            return ResponseEntity.ok(Map.of("data", trend, "period", "weekly"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve weekly borrow trend"));
        }
    }

    @Operation(
            summary = "Get monthly borrow trend",
            description = "Retrieves borrowing statistics for the past month",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Monthly borrow trend retrieved successfully",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = Map.class))),
            })
    @GetMapping("/borrow-trends/monthly")
    public ResponseEntity<?> getMonthlyBorrowTrend() {
        try {
            Map<String, Long> trend = statisticsService.getBorrowTrendByMonth();
            return ResponseEntity.ok(Map.of("data", trend, "period", "monthly"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve monthly borrow trend"));
        }
    }

    @Operation(
            summary = "Get book inventory statistics",
            description = "Retrieves comprehensive book inventory statistics including availability and status",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Book inventory statistics retrieved successfully",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "Access denied - admin role required",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/inventory")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getBookInventoryStatistics() {
        try {
            Map<?, Map<String, Long>> statistics = statisticsService.getBookInventoryStatistics();
            return ResponseEntity.ok(Map.of("statistics", statistics, "timestamp", System.currentTimeMillis()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve book inventory statistics"));
        }
    }

    @Operation(
            summary = "Get user behavior analysis",
            description = "Retrieves analysis of user behavior patterns including borrowing frequency and preferences",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "User behavior analysis retrieved successfully",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "Access denied - admin role required",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/user-behavior")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserBehaviorAnalysis() {
        try {
            Map<String, Long> analysis = statisticsService.getUserBehaviorAnalysis();
            return ResponseEntity.ok(Map.of("analysis", analysis, "timestamp", System.currentTimeMillis()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve user behavior analysis"));
        }
    }

    @Operation(
            summary = "Get library dashboard summary",
            description = "Retrieves a summary of key library statistics for dashboard display",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Dashboard summary retrieved successfully",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = Map.class))),
            })
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardSummary() {
        try {
            // Combine multiple statistics for dashboard
            Map<String, Long> userBehavior = statisticsService.getUserBehaviorAnalysis();
            Map<?, Map<String, Long>> inventory = statisticsService.getBookInventoryStatistics();

            return ResponseEntity.ok(Map.of(
                    "userBehavior", userBehavior,
                    "inventory", inventory,
                    "timestamp", System.currentTimeMillis()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve dashboard summary"));
        }
    }
}
