package com.aaron212.onlinelibrarymanagement.backend.controller;

import com.aaron212.onlinelibrarymanagement.backend.dto.UserDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.UserFullDto;
import com.aaron212.onlinelibrarymanagement.backend.mapper.UserMapper;
import com.aaron212.onlinelibrarymanagement.backend.model.User;
import com.aaron212.onlinelibrarymanagement.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/getUserById")
    public ResponseEntity<UserDto> getUserById(@RequestParam Long id) {
        User user = service.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        UserDto response = UserMapper.INSTANCE.toUserRecord(user);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getUserByUsername")
    public ResponseEntity<UserDto> getUserByUsername(@RequestParam String username) {
        User user = service.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        UserDto response = UserMapper.INSTANCE.toUserRecord(user);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/myDetails")
    public ResponseEntity<UserFullDto> getUserRole(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            UserFullDto userFullRecord = UserMapper.INSTANCE.toUserFullRecord(user);

            return ResponseEntity.ok(userFullRecord);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @PostMapping("/editMyDetails")
    public ResponseEntity<UserFullDto> editMyDetails(@RequestBody UserFullDto userFullDto,
                                                     Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            User updatedUser = service.updateUserDetails(user, userFullDto);
            UserFullDto response = UserMapper.INSTANCE.toUserFullRecord(updatedUser);

            return ResponseEntity.ok(response);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }
}
