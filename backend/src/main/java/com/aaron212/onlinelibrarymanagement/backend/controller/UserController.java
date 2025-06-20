package com.aaron212.onlinelibrarymanagement.backend.controller;

import com.aaron212.onlinelibrarymanagement.backend.dto.UserUpdateDto;
import com.aaron212.onlinelibrarymanagement.backend.projection.UserFullProjection;
import com.aaron212.onlinelibrarymanagement.backend.projection.UserPublicProjection;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get user public details by ID",
            description = "Retrieves a user by their unique ID",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "User found",
                        content = @Content(schema = @Schema(implementation = UserPublicProjection.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @Parameter(description = "User ID", required = true, example = "1") @PathVariable @Positive Long id) {
        try {
            UserPublicProjection user = userService
                    .findPublicById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            return ResponseEntity.ok(user);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
    }

    @Operation(
            summary = "Get user by username",
            description = "Retrieves a user by their username",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "User found",
                        content = @Content(schema = @Schema(implementation = UserPublicProjection.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(
            @Parameter(description = "Username", required = true, example = "john_doe") @PathVariable @NotBlank
                    String username) {
        try {
            UserPublicProjection user = userService
                    .findPublicByUsername(username)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            return ResponseEntity.ok(user);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
    }

    @Operation(
            summary = "Get current user details",
            description = "Retrieves the full details of the currently authenticated user",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "User details retrieved successfully",
                        content = @Content(schema = @Schema(implementation = UserFullProjection.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserDetails(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }

        try {
            UserFullProjection user = userService
                    .findFullByUsername(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            return ResponseEntity.ok(user);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
    }

    @Operation(
            summary = "Update current user details",
            description = "Updates the details of the currently authenticated user",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "User details updated successfully",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid user data",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUserDetails(
            @Valid @RequestBody UserUpdateDto userModifyDto, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }

        try {
            userService.updateUserDetails(authentication.getName(), userModifyDto);
            return ResponseEntity.ok(Map.of("message", "User details updated successfully"));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Failed to update user details"));
        }
    }

    @Operation(
            summary = "Get all users (Admin only)",
            description = "Retrieves all users with pagination (admin only)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Users retrieved successfully",
                        content = @Content(schema = @Schema(implementation = Page.class))),
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
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(
            @Parameter(description = "Page number", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Search keyword") @RequestParam(required = false) String search) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserPublicProjection> users;
            
            if (search != null && !search.trim().isEmpty()) {
                users = userService.searchUsers(search.trim(), pageable);
            } else {
                users = userService.findAllUsers(pageable);
            }
            
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Failed to retrieve users"));
        }
    }
}
