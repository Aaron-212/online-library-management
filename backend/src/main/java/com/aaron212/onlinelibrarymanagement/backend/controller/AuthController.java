package com.aaron212.onlinelibrarymanagement.backend.controller;

import com.aaron212.onlinelibrarymanagement.backend.dto.UserLoginDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.UserRegisterDto;
import com.aaron212.onlinelibrarymanagement.backend.projection.UserFullProjection;
import com.aaron212.onlinelibrarymanagement.backend.service.JwtService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided registration details")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "User registered successfully",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid registration data",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "409",
                        description = "User already exists",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody UserRegisterDto registerRequest) {
        try {
            userService.addUser(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Registration failed"));
        }
    }

    @Operation(summary = "Authenticate user", description = "Authenticates user credentials and returns a JWT token")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Authentication successful",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "Invalid credentials",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody UserLoginDto loginRequest) {
        if (loginRequest.usernameOrEmail() == null || loginRequest.password() == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Username/email and password are required"));
        }
        UserFullProjection user;
        if (loginRequest.usernameOrEmail().contains("@")) {
            // If the usernameOrEmail contains '@', treat it as an email
            user = userService
                    .findByEmail(loginRequest.usernameOrEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            // Otherwise, treat it as a username
            user = userService
                    .findByUsername(loginRequest.usernameOrEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), loginRequest.password()));
            String token = jwtService.generateToken(user.getUsername(), user.getLastUpdateTime());

            return ResponseEntity.ok(Map.of("token", token, "message", "Login successful"));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password"));
        }
    }

    @Operation(
            summary = "Change user password",
            description = "Changes the password for the authenticated user",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Password changed successfully",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid password change request",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @Parameter(description = "Current password", required = true) @RequestParam @NotBlank String oldPassword,
            @Parameter(description = "New password", required = true) @RequestParam @NotBlank String newPassword,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }

        try {
            userService.changePassword(authentication.getName(), oldPassword, newPassword);

            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Failed to change password"));
        }
    }

    @Operation(
            summary = "Check authentication status",
            description = "Verifies if the user is currently authenticated",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "User is authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyToken() {
        return ResponseEntity.ok(Map.of("message", "Token is valid"));
    }
}
