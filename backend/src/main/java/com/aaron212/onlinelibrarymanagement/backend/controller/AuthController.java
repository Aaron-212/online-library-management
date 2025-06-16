package com.aaron212.onlinelibrarymanagement.backend.controller;

import com.aaron212.onlinelibrarymanagement.backend.dto.LoginRequest;
import com.aaron212.onlinelibrarymanagement.backend.dto.RegisterRequest;
import com.aaron212.onlinelibrarymanagement.backend.model.User;
import com.aaron212.onlinelibrarymanagement.backend.service.JwtService;
import com.aaron212.onlinelibrarymanagement.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
    public ResponseEntity<String> addNewUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            service.addUser(registerRequest);
            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to register user", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                    loginRequest.getPassword()));

            User user = service.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            String token = jwtService.generateToken(loginRequest.getUsername(), user.getLastUpdateTime());
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Invalid username or password!", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword,
                                                 Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                service.changePassword(authentication.getName(), oldPassword, newPassword);
                return ResponseEntity.ok("Password changed successfully");
            } catch (Exception e) {
                return new ResponseEntity<>("Failed to change password: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("User not authenticated", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/check")
    public ResponseEntity<String> checkAuthentication() {
        return ResponseEntity.ok("You are authenticated!");
    }
}
