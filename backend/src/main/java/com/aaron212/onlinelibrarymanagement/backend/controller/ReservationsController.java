package com.aaron212.onlinelibrarymanagement.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservations", description = "Stub endpoints for book reservations. Full implementation pending.")
public class ReservationsController {

    @Operation(
            summary = "Get current user's reservations (stub)",
            description = "Returns an empty paginated response to satisfy frontend routing until the feature is fully implemented.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Request successful", content = @Content(schema = @Schema(implementation = Page.class))),
                @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/user")
    public ResponseEntity<Page<?>> getCurrentUserReservations(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        Page<?> emptyPage = new PageImpl<>(List.of(), PageRequest.of(page, size), 0);
        return ResponseEntity.ok(emptyPage);
    }

    @Operation(
            summary = "Cancel reservation (stub)",
            description = "Cancels a reservation. Currently returns success without effect.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Reservation cancelled", content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Map<String, String>> cancelReservation(
            Authentication authentication,
            @PathVariable Long reservationId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        // TODO: Replace with real cancellation logic once implemented
        return ResponseEntity.ok(Map.of("message", "Reservation cancelled (stub)"));
    }
}