package com.aaron212.onlinelibrarymanagement.backend.controller;

import com.aaron212.onlinelibrarymanagement.backend.dto.FavoriteCreateDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.FavoriteDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.FavoriteResponseDto;
import com.aaron212.onlinelibrarymanagement.backend.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/favorites")
@Tag(name = "Favorites", description = "Endpoints for managing user favorite books")
public class FavoritesController {

    private final FavoriteService favoriteService;

    public FavoritesController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @Operation(
            summary = "Get current user's favorites",
            description = "Returns a paginated list of the current user's favorite books")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Favorites retrieved successfully", 
                    content = @Content(schema = @Schema(implementation = Page.class))),
                @ApiResponse(responseCode = "401", description = "User not authenticated", 
                    content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUserFavorites(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<FavoriteDto> favorites = favoriteService.getUserFavorites(authentication.getName(), pageable);
        return ResponseEntity.ok(favorites);
    }

    @Operation(
            summary = "Add a book to favorites",
            description = "Adds a book to the current user's favorites")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Favorite added successfully", 
                    content = @Content(schema = @Schema(implementation = FavoriteResponseDto.class))),
                @ApiResponse(responseCode = "400", description = "Invalid request or book already favorited"),
                @ApiResponse(responseCode = "401", description = "User not authenticated"),
                @ApiResponse(responseCode = "404", description = "Book not found")
            })
    @PostMapping
    public ResponseEntity<?> addFavorite(
            Authentication authentication, 
            @Valid @RequestBody FavoriteCreateDto createDto) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        FavoriteResponseDto favorite = favoriteService.addFavorite(authentication.getName(), createDto);
        return ResponseEntity.ok(favorite);
    }

    @Operation(
            summary = "Remove favorite by ID",
            description = "Removes a favorite book by its favorite ID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Favorite removed successfully"),
                @ApiResponse(responseCode = "401", description = "User not authenticated"),
                @ApiResponse(responseCode = "403", description = "Favorite doesn't belong to the user"),
                @ApiResponse(responseCode = "404", description = "Favorite not found")
            })
    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<Map<String, String>> removeFavorite(
            Authentication authentication, 
            @PathVariable Long favoriteId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        favoriteService.removeFavorite(authentication.getName(), favoriteId);
        return ResponseEntity.ok(Map.of("message", "Favorite removed successfully"));
    }

    @Operation(
            summary = "Remove favorite by book ID",
            description = "Removes a book from favorites by book ID")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Favorite removed successfully"),
                @ApiResponse(responseCode = "401", description = "User not authenticated"),
                @ApiResponse(responseCode = "404", description = "Book not found in favorites")
            })
    @DeleteMapping("/book/{bookId}")
    public ResponseEntity<Map<String, String>> removeFavoriteByBook(
            Authentication authentication, 
            @PathVariable Long bookId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        favoriteService.removeFavoriteByBook(authentication.getName(), bookId);
        return ResponseEntity.ok(Map.of("message", "Favorite removed successfully"));
    }

    @Operation(
            summary = "Check if book is favorited",
            description = "Checks whether a book is in the current user's favorites")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Check completed successfully"),
                @ApiResponse(responseCode = "401", description = "User not authenticated")
            })
    @GetMapping("/check/{bookId}")
    public ResponseEntity<?> isFavorite(
            Authentication authentication, 
            @PathVariable Long bookId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        boolean isFavorite = favoriteService.isFavorite(authentication.getName(), bookId);
        return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
    }

    @Operation(
            summary = "Get user's favorites count",
            description = "Returns the total count of user's favorite books")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
                @ApiResponse(responseCode = "401", description = "User not authenticated")
            })
    @GetMapping("/count")
    public ResponseEntity<?> getFavoritesCount(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        long count = favoriteService.getUserFavoritesCount(authentication.getName());
        return ResponseEntity.ok(Map.of("count", count));
    }
}