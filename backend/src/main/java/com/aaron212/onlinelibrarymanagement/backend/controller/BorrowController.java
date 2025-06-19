package com.aaron212.onlinelibrarymanagement.backend.controller;

import com.aaron212.onlinelibrarymanagement.backend.dto.BorrowDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BorrowResponseDto;
import com.aaron212.onlinelibrarymanagement.backend.mapper.BorrowMapper;
import com.aaron212.onlinelibrarymanagement.backend.model.Borrow;
import com.aaron212.onlinelibrarymanagement.backend.service.BorrowService;
import com.aaron212.onlinelibrarymanagement.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/borrow")
@Tag(name = "Borrowing", description = "Book borrowing and return management endpoints")
public class BorrowController {

    private final BorrowService borrowService;
    private final UserService userService;

    public BorrowController(BorrowService borrowService, UserService userService) {
        this.borrowService = borrowService;
        this.userService = userService;
    }

    @Operation(
            summary = "Borrow a book",
            description = "Allows a user to borrow a specific book copy",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Book borrowed successfully",
                        content = @Content(schema = @Schema(implementation = BorrowResponseDto.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request or book unavailable",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(
            @Parameter(description = "User ID", required = true, example = "1") @RequestParam @Positive Long userId,
            @Parameter(description = "Book copy ID", required = true, example = "1") @RequestParam @Positive Long copyId) {
        try {
            Borrow borrow = borrowService.borrowBook(userId, copyId);
            BorrowResponseDto responseDto = BorrowMapper.INSTANCE.toBorrowResponseDto(borrow);
            responseDto = new BorrowResponseDto(
                responseDto.borrowId(),
                responseDto.userId(),
                responseDto.username(),
                responseDto.copyId(),
                responseDto.bookTitle(),
                responseDto.isbn(),
                responseDto.borrowTime(),
                responseDto.returnTime(),
                responseDto.status(),
                "借书成功，应还日期：" + borrow.getReturnTime().toLocalDate()
            );
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Return a book",
            description = "Allows a user to return a borrowed book",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Book returned successfully",
                        content = @Content(schema = @Schema(implementation = BorrowResponseDto.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request or book not borrowed by user",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PostMapping("/return")
    public ResponseEntity<?> returnBook(
            @Parameter(description = "User ID", required = true, example = "1") @RequestParam @Positive Long userId,
            @Parameter(description = "Book copy ID", required = true, example = "1") @RequestParam @Positive Long copyId) {
        try {
            Borrow borrow = borrowService.returnBook(userId, copyId);
            BorrowResponseDto responseDto = BorrowMapper.INSTANCE.toBorrowResponseDto(borrow);
            String message = borrow.getStatus() == Borrow.Status.OVERDUE 
                ? "还书成功（已逾期，罚金：" + borrow.getFine() + "元）" 
                : "还书成功";
            responseDto = new BorrowResponseDto(
                responseDto.borrowId(),
                responseDto.userId(),
                responseDto.username(),
                responseDto.copyId(),
                responseDto.bookTitle(),
                responseDto.isbn(),
                responseDto.borrowTime(),
                responseDto.returnTime(),
                responseDto.status(),
                message
            );
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Renew a book",
            description = "Allows a user to extend the borrowing period of a book",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Book renewed successfully",
                        content = @Content(schema = @Schema(implementation = BorrowResponseDto.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request or renewal not allowed",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PostMapping("/renew")
    public ResponseEntity<?> renewBook(
            @Parameter(description = "User ID", required = true, example = "1") @RequestParam @Positive Long userId,
            @Parameter(description = "Book copy ID", required = true, example = "1") @RequestParam @Positive Long copyId) {
        try {
            Borrow borrow = borrowService.renewBook(userId, copyId);
            BorrowResponseDto responseDto = BorrowMapper.INSTANCE.toBorrowResponseDto(borrow);
            responseDto = new BorrowResponseDto(
                responseDto.borrowId(),
                responseDto.userId(),
                responseDto.username(),
                responseDto.copyId(),
                responseDto.bookTitle(),
                responseDto.isbn(),
                responseDto.borrowTime(),
                responseDto.returnTime(),
                responseDto.status(),
                "续借成功，新还书日期：" + borrow.getReturnTime().toLocalDate()
            );
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Reserve a book",
            description = "Allows a user to reserve a book when all copies are currently borrowed",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Book reserved successfully",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request or reservation not allowed",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PostMapping("/reserve")
    public ResponseEntity<?> reserveBook(
            @Parameter(description = "User ID", required = true, example = "1") @RequestParam @Positive Long userId,
            @Parameter(description = "Book ID", required = true, example = "1") @RequestParam @Positive Long bookId) {
        try {
            borrowService.reserveBook(userId, bookId);
            return ResponseEntity.ok(Map.of("message", "预约成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Get borrowing history",
            description = "Retrieves the complete borrowing history for a specific user",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Borrowing history retrieved successfully",
                        content = @Content(schema = @Schema(implementation = List.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid user ID",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getBorrowHistory(
            @Parameter(description = "User ID", required = true, example = "1") @PathVariable @Positive Long userId) {
        try {
            List<Borrow> history = borrowService.getBorrowHistory(userId);
            List<BorrowDto> historyDto = history.stream()
                    .map(BorrowMapper.INSTANCE::toBorrowDto)
                    .toList();
            return ResponseEntity.ok(historyDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Get my borrowing history",
            description = "Retrieves the complete borrowing history for the currently authenticated user",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Borrowing history retrieved successfully",
                        content = @Content(schema = @Schema(implementation = List.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/my-history")
    public ResponseEntity<?> getMyBorrowHistory(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }

        try {
            Long userId = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();
            List<Borrow> history = borrowService.getBorrowHistory(userId);
            List<BorrowDto> historyDto = history.stream()
                    .map(BorrowMapper.INSTANCE::toBorrowDto)
                    .toList();
            return ResponseEntity.ok(historyDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Get current borrowings",
            description = "Retrieves all currently active borrowings for a specific user",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Current borrowings retrieved successfully",
                        content = @Content(schema = @Schema(implementation = List.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid user ID",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    @GetMapping("/current/{userId}")
    public ResponseEntity<?> getCurrentBorrowings(
            @Parameter(description = "User ID", required = true, example = "1") @PathVariable @Positive Long userId) {
        try {
            List<Borrow> currentBorrowings = borrowService.getCurrentBorrowings(userId);
            List<BorrowDto> currentDto = currentBorrowings.stream()
                    .map(BorrowMapper.INSTANCE::toBorrowDto)
                    .toList();
            return ResponseEntity.ok(currentDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Get my current borrowings",
            description = "Retrieves all currently active borrowings for the authenticated user",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Current borrowings retrieved successfully",
                        content = @Content(schema = @Schema(implementation = List.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/my-current")
    public ResponseEntity<?> getMyCurrentBorrowings(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }

        try {
            Long userId = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();
            List<Borrow> currentBorrowings = borrowService.getCurrentBorrowings(userId);
            List<BorrowDto> currentDto = currentBorrowings.stream()
                    .map(BorrowMapper.INSTANCE::toBorrowDto)
                    .toList();
            return ResponseEntity.ok(currentDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Get overdue borrowings",
            description = "Retrieves all overdue borrowings across the system (admin only)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Overdue borrowings retrieved successfully",
                        content = @Content(schema = @Schema(implementation = List.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "Access denied - admin role required",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/overdue")
    public ResponseEntity<?> getOverdueBorrowings() {
        try {
            List<Borrow> overdueBorrowings = borrowService.getOverdueBorrowings();
            List<BorrowDto> overdueDto = overdueBorrowings.stream()
                    .map(BorrowMapper.INSTANCE::toBorrowDto)
                    .toList();
            return ResponseEntity.ok(overdueDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
