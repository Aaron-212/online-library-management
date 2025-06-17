package com.aaron212.onlinelibrarymanagement.backend.controller;

import com.aaron212.onlinelibrarymanagement.backend.dto.UserDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.UserFullDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.UserModifyDto;
import com.aaron212.onlinelibrarymanagement.backend.mapper.UserMapper;
import com.aaron212.onlinelibrarymanagement.backend.model.User;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
        summary = "Get user by ID",
        description = "Retrieves a user by their unique ID",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found",
                content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable @Positive Long id) {
        try {
            User user = userService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            UserDto response = UserMapper.INSTANCE.toUserRecord(user);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
    }

    @Operation(
        summary = "Get user by username",
        description = "Retrieves a user by their username",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found",
                content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(
            @Parameter(description = "Username", required = true, example = "john_doe")
            @PathVariable @NotBlank String username) {
        try {
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            UserDto response = UserMapper.INSTANCE.toUserRecord(user);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
    }

    @Operation(
        summary = "Get current user details",
        description = "Retrieves the full details of the currently authenticated user",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
                content = @Content(schema = @Schema(implementation = UserFullDto.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated",
                content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserDetails(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }

        try {
            User user = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            UserFullDto userFullRecord = UserMapper.INSTANCE.toUserFullRecord(user);
            return ResponseEntity.ok(userFullRecord);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
    }

    @Operation(
        summary = "Update current user details",
        description = "Updates the details of the currently authenticated user",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User details updated successfully",
                content = @Content(schema = @Schema(implementation = UserFullDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid user data",
                content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated",
                content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUserDetails(
            @Valid @RequestBody UserModifyDto userModifyDto,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }

        try {
            User user = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            User updatedUser = userService.updateUserDetails(user, userModifyDto);
            UserFullDto response = UserMapper.INSTANCE.toUserFullRecord(updatedUser);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to update user details"));
        }
    }
}
