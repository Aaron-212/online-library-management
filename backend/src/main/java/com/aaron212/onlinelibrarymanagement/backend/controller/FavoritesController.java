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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/favorites")
@Tag(name = "Favorites", description = "Stub endpoints for user favorite books. Full implementation pending.")
public class FavoritesController {

    @Operation(
            summary = "Get current user's favorites (stub)",
            description = "Returns an empty paginated response to satisfy frontend routing until the feature is fully implemented.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Request successful", content = @Content(schema = @Schema(implementation = Page.class))),
                @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/user")
    public ResponseEntity<Page<?>> getCurrentUserFavorites(
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
            summary = "Add favorite (stub)",
            description = "Adds a favorite book for the current user. Currently returns success without effect.")
    @PostMapping
    public ResponseEntity<Map<String, String>> addFavorite(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }
        return ResponseEntity.ok(Map.of("message", "Favorite added (stub)"));
    }

    @Operation(
            summary = "Remove favorite (stub)",
            description = "Removes a favorite by ID. Currently returns success without effect.")
    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<Map<String, String>> removeFavorite(Authentication authentication, @PathVariable Long favoriteId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }
        return ResponseEntity.ok(Map.of("message", "Favorite removed (stub)"));
    }
}