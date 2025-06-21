package com.aaron212.onlinelibrarymanagement.backend.controller;

import com.aaron212.onlinelibrarymanagement.backend.dto.FeeCalculationDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.FeeResponseDto;
import com.aaron212.onlinelibrarymanagement.backend.mapper.FeeMapper;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Overdue fine calculated successfully",
                        content = @Content(schema = @Schema(implementation = FeeResponseDto.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Borrow record not found",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "Access denied - admin role required",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PostMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> calculateOverdueFine(@Valid @RequestBody FeeCalculationDto requestDto) {
        try {
            Borrow result = feeService.calculateOverdueFine(requestDto.borrowId());
            if (result != null) {
                FeeResponseDto responseDto = FeeMapper.INSTANCE.toFeeResponseDto(result, 
                    "逾期罚款计算成功，罚金：" + result.getFine() + "元");
                return ResponseEntity.ok(responseDto);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Borrow record not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to calculate overdue fine"));
        }
    }

    @Operation(
            summary = "Calculate book compensation",
            description = "Calculates and applies book compensation fee for a specific borrow record",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Book compensation calculated successfully",
                        content = @Content(schema = @Schema(implementation = FeeResponseDto.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Borrow record not found",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "Access denied - admin role required",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PostMapping("/compensation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> calculateCompensation(@Valid @RequestBody FeeCalculationDto requestDto) {
        try {
            Borrow result = feeService.calculateBookCompensation(requestDto.borrowId());
            if (result != null) {
                FeeResponseDto responseDto = FeeMapper.INSTANCE.toFeeResponseDto(result, 
                    "图书赔偿费用计算成功，赔偿金：" + result.getFine() + "元");
                return ResponseEntity.ok(responseDto);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Borrow record not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to calculate book compensation"));
        }
    }

    @Operation(
            summary = "Get current user's unpaid fees",
            description = "Retrieves all unpaid fees for the currently authenticated user",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Unpaid fees retrieved successfully",
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
    @GetMapping("/me/unpaid")
    public ResponseEntity<?> getCurrentUserUnpaidFees(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }

        try {
            User user = userService
                    .findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<Borrow> fees = feeService.getUnpaidFeesByUser(user.getId());
            return ResponseEntity.ok(fees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
    }

    @Operation(
            summary = "Get user's unpaid fees by user ID",
            description = "Retrieves all unpaid fees for a specific user (admin only)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Unpaid fees retrieved successfully",
                        content = @Content(schema = @Schema(implementation = List.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "Access denied - admin role required",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/users/{userId}/unpaid")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserUnpaidFees(
            @Parameter(description = "User ID", required = true, example = "1") @PathVariable @Positive Long userId) {
        try {
            // Verify user exists
            userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

            List<Borrow> fees = feeService.getUnpaidFeesByUser(userId);
            return ResponseEntity.ok(fees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
    }

    @Operation(
            summary = "Get all fees for current user (stub)",
            description = "Temporary stub endpoint to align with frontend '/fees/user' path. Returns an empty page until full implementation is provided.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Request successful", content = @Content(schema = @Schema(implementation = Page.class))),
                @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/user")
    public ResponseEntity<?> getAllFeesForCurrentUser(
            Authentication authentication,
            @Parameter(description = "Page number", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }

        // TODO: Replace with real implementation once service layer supports pagination
        Page<?> emptyPage = new PageImpl<>(List.of(), PageRequest.of(page, size), 0);
        return ResponseEntity.ok(emptyPage);
    }
}
