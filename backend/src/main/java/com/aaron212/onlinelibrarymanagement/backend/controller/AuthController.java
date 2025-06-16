package com.aaron212.onlinelibrarymanagement.backend.controller;

import com.aaron212.onlinelibrarymanagement.backend.dto.ApiResponse;
import com.aaron212.onlinelibrarymanagement.backend.dto.LoginRequest;
import com.aaron212.onlinelibrarymanagement.backend.dto.LoginResponse;
import com.aaron212.onlinelibrarymanagement.backend.dto.RegisterRequest;
import com.aaron212.onlinelibrarymanagement.backend.service.JwtService;
import com.aaron212.onlinelibrarymanagement.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService service;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    AuthController(UserService service, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.service = service;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> addNewUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            service.addUser(registerRequest);
            ApiResponse apiResponse = new ApiResponse(true, "User registered successfully");
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse(false, e.getMessage());
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername()
                            , loginRequest.getPassword()));

            String token = jwtService.generateToken(loginRequest.getUsername());
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(new ApiResponse(false, "Invalid username or password!"), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/role")
    public ResponseEntity<String> getUserRole(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok(authentication.getAuthorities()
                    .toString());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User is not authenticated");
        }
    }

    @GetMapping("/check")
    public ResponseEntity<String> checkAuthentication() {
        return ResponseEntity.ok("You are authenticated!");
    }
}
