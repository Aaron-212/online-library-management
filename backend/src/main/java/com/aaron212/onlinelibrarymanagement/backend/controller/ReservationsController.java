package com.aaron212.onlinelibrarymanagement.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.aaron212.onlinelibrarymanagement.backend.service.ReservationService;
import com.aaron212.onlinelibrarymanagement.backend.model.Reservation;

@RestController
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservations", description = "Book reservation management endpoints")
public class ReservationsController {

    private final ReservationService reservationService;

    public ReservationsController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(
            summary = "Get current user's active reservations",
            description = "Returns a list of current waiting reservations for the authenticated user.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Request successful", content = @Content(schema = @Schema(implementation = List.class))),
                @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUserReservations(
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        Long userId = ((com.aaron212.onlinelibrarymanagement.backend.model.User) authentication.getPrincipal()).getId();
        List<?> reservations = reservationService.getUserActiveReservations(userId);
        return ResponseEntity.ok(reservations);
    }

    @Operation(
            summary = "Cancel reservation",
            description = "Cancels a waiting reservation belonging to the authenticated user.")
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
        Long userId = ((com.aaron212.onlinelibrarymanagement.backend.model.User) authentication.getPrincipal()).getId();
        try {
            reservationService.cancelReservation(userId, reservationId);
            return ResponseEntity.ok(Map.of("message", "Reservation cancelled"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Create reservation", description = "Reserve a book by bookId")
    @PostMapping("/{bookId}")
    public ResponseEntity<?> createReservation(Authentication authentication, @PathVariable Long bookId){
        if(authentication == null || !authentication.isAuthenticated()){
            return ResponseEntity.status(401).body(Map.of("error","User not authenticated"));
        }
        Long userId = ((com.aaron212.onlinelibrarymanagement.backend.model.User) authentication.getPrincipal()).getId();
        try{
            Reservation reservation = reservationService.createReservation(userId, bookId);
            return ResponseEntity.ok(reservation);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}