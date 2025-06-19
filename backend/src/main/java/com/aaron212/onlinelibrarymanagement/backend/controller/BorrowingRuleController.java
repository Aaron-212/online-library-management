package com.aaron212.onlinelibrarymanagement.backend.controller;

import com.aaron212.onlinelibrarymanagement.backend.dto.BorrowingRuleDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BorrowingRuleUpdateDto;
import com.aaron212.onlinelibrarymanagement.backend.service.BorrowingRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/borrowing-rules")
@Tag(name = "Borrowing Rules", description = "Borrowing rules management endpoints")
public class BorrowingRuleController {

    private final BorrowingRuleService borrowingRuleService;

    public BorrowingRuleController(BorrowingRuleService borrowingRuleService) {
        this.borrowingRuleService = borrowingRuleService;
    }

    @Operation(
            summary = "Get all borrowing rules",
            description = "Retrieves all borrowing rules in the system")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Borrowing rules retrieved successfully",
                        content = @Content(schema = @Schema(implementation = List.class)))
            })
    @GetMapping
    public ResponseEntity<List<BorrowingRuleDto>> getAllRules() {
        List<BorrowingRuleDto> rules = borrowingRuleService.getAllRules();
        return ResponseEntity.ok(rules);
    }

    @Operation(
            summary = "Get borrowing rule by key",
            description = "Retrieves a specific borrowing rule by its key")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Borrowing rule found",
                        content = @Content(schema = @Schema(implementation = BorrowingRuleDto.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Borrowing rule not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/{ruleKey}")
    public ResponseEntity<?> getRuleByKey(
            @Parameter(description = "Rule key", required = true, example = "MAX_BORROW_BOOKS")
            @PathVariable
            @NotBlank
            String ruleKey) {
        Optional<BorrowingRuleDto> rule = borrowingRuleService.getRuleByKey(ruleKey);
        if (rule.isPresent()) {
            return ResponseEntity.ok(rule.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Borrowing rule not found: " + ruleKey));
        }
    }

    @Operation(
            summary = "Update borrowing rule",
            description = "Updates an existing borrowing rule with the provided details. Only administrators can update rules.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Borrowing rule updated successfully",
                        content = @Content(schema = @Schema(implementation = BorrowingRuleDto.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid rule data",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Borrowing rule not found",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "Access denied - admin role required",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{ruleKey}")
    public ResponseEntity<?> updateRule(
            @Parameter(description = "Rule key", required = true, example = "MAX_BORROW_BOOKS")
            @PathVariable
            @NotBlank
            String ruleKey,
            @Valid @RequestBody BorrowingRuleUpdateDto updateDto) {
        try {
            BorrowingRuleDto updatedRule = borrowingRuleService.updateRule(ruleKey, updateDto);
            return ResponseEntity.ok(updatedRule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Borrowing rule not found: " + ruleKey));
        }
    }

    @Operation(
            summary = "Get borrowing rules summary",
            description = "Retrieves a summary of key borrowing rules for display purposes")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Borrowing rules summary retrieved successfully",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getRulesSummary() {
        try {
            Map<String, Object> summary = Map.of(
                    "maxBorrowBooks", borrowingRuleService.getIntegerRule(BorrowingRuleService.MAX_BORROW_BOOKS),
                    "loanPeriodDays", borrowingRuleService.getIntegerRule(BorrowingRuleService.LOAN_PERIOD_DAYS),
                    "renewalPeriodDays", borrowingRuleService.getIntegerRule(BorrowingRuleService.RENEWAL_PERIOD_DAYS),
                    "finePerDay", borrowingRuleService.getDecimalRule(BorrowingRuleService.FINE_PER_DAY),
                    "maxRenewalTimes", borrowingRuleService.getIntegerRule(BorrowingRuleService.MAX_RENEWAL_TIMES),
                    "allowRenewals", borrowingRuleService.getBooleanRule(BorrowingRuleService.ALLOW_RENEWALS),
                    "advanceReserveDays", borrowingRuleService.getIntegerRule(BorrowingRuleService.ADVANCE_RESERVE_DAYS)
            );
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve rules summary"));
        }
    }
}