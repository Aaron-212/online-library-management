package com.aaron212.onlinelibrarymanagement.backend.controller;

import com.aaron212.onlinelibrarymanagement.backend.model.Borrow;
import com.aaron212.onlinelibrarymanagement.backend.model.User;
import com.aaron212.onlinelibrarymanagement.backend.service.FeeService;
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
@RequestMapping("/api/v1/fees")
@Tag(name = "Fees", description = "Fee management endpoints for library fines and compensation")
public class FeeController {

    private final FeeService feeService;
    private final UserService userService;

    public FeeController(FeeService feeService, UserService userService) {
        this.feeService = feeService;
        this.userService = userService;
    }

    @Operation(
        summary = "Calculate overdue fine",
        description = "Calculates and applies overdue fine for a specific borrow record",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Overdue fine calculated successfully",
                content = @Content(schema = @Schema(implementation = Borrow.class))),
        @ApiResponse(responseCode = "404", description = "Borrow record not found",
                content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - librarian role required",
                content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/overdue/{borrowId}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> calculateOverdueFine(
            @Parameter(description = "Borrow record ID", required = true, example = "1")
            @PathVariable @Positive Long borrowId) {
        try {
            Borrow result = feeService.calculateOverdueFine(borrowId);
            if (result != null) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Borrow record not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to calculate overdue fine"));
        }
    }

    @Operation(
        summary = "Calculate book compensation",
        description = "Calculates and applies book compensation fee for a specific borrow record",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Book compensation calculated successfully",
                content = @Content(schema = @Schema(implementation = Borrow.class))),
        @ApiResponse(responseCode = "404", description = "Borrow record not found",
                content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - librarian role required",
                content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/compensation/{borrowId}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> calculateCompensation(
            @Parameter(description = "Borrow record ID", required = true, example = "1")
            @PathVariable @Positive Long borrowId) {
        try {
            Borrow result = feeService.calculateBookCompensation(borrowId);
            if (result != null) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Borrow record not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to calculate book compensation"));
        }
    }

    @Operation(
        summary = "Get current user's unpaid fees",
        description = "Retrieves all unpaid fees for the currently authenticated user",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unpaid fees retrieved successfully",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated",
                content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/me/unpaid")
    public ResponseEntity<?> getCurrentUserUnpaidFees(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }

        try {
            User user = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<Borrow> fees = feeService.getUnpaidFeesByUser(user.getId());
            return ResponseEntity.ok(fees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
    }

    @Operation(
        summary = "Get user's unpaid fees by user ID",
        description = "Retrieves all unpaid fees for a specific user (librarian only)",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unpaid fees retrieved successfully",
                content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - librarian role required",
                content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/users/{userId}/unpaid")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<?> getUserUnpaidFees(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable @Positive Long userId) {
        try {
            // Verify user exists
            userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<Borrow> fees = feeService.getUnpaidFeesByUser(userId);
            return ResponseEntity.ok(fees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
    }
}
