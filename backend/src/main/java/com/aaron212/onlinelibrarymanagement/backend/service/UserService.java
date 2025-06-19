package com.aaron212.onlinelibrarymanagement.backend.service;

import com.aaron212.onlinelibrarymanagement.backend.dto.UserModifyDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.UserRegisterDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.UserUpdateDto;
import com.aaron212.onlinelibrarymanagement.backend.model.User;
import com.aaron212.onlinelibrarymanagement.backend.projection.UserFullProjection;
import com.aaron212.onlinelibrarymanagement.backend.projection.UserPublicProjection;
import com.aaron212.onlinelibrarymanagement.backend.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.ScopedValue;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return (UserDetails) user;
    }

    public void addUser(UserRegisterDto registerRequest) throws RuntimeException {
        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new RuntimeException("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        User newUser = new User();
        newUser.setUsername(registerRequest.username());
        newUser.setEmail(registerRequest.email());
        newUser.setPasswordHash(passwordEncoder.encode(registerRequest.password()));
        newUser.setRole(User.Role.USER);
        userRepository.save(newUser);
    }

    public Optional<UserFullProjection> findFullByUsername(String username) {
        return userRepository.findFullByUsername(username);
    }

    public Optional<UserFullProjection> findFullById(Long id) {
        return userRepository.findFullById(id);
    }

    public Optional<UserFullProjection> findFullByEmail(String email) {
        return userRepository.findFullByEmail(email);
    }

    public Optional<UserPublicProjection> findPublicByUsername(String username) {
        return userRepository.findPublicByUsername(username);
    }

    public Optional<UserPublicProjection> findPublicById(Long id) {
        return userRepository.findPublicById(id);
    }

    public User updateUserDetails(String username, UserUpdateDto userModifyDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(userModifyDto.username());
        user.setEmail(userModifyDto.email());
        // Note: Password should not be updated here unless explicitly provided in the DTO
        return userRepository.save(user);
    }

    public void changePassword(String name, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(name).orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setLastUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
        userRepository.save(user);
    }
}
