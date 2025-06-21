package com.aaron212.onlinelibrarymanagement.backend.service;

import com.aaron212.onlinelibrarymanagement.backend.dto.UserRegisterDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.UserUpdateDto;
import com.aaron212.onlinelibrarymanagement.backend.exception.BusinessLogicException;
import com.aaron212.onlinelibrarymanagement.backend.exception.DuplicateResourceException;
import com.aaron212.onlinelibrarymanagement.backend.exception.ResourceNotFoundException;
import com.aaron212.onlinelibrarymanagement.backend.model.User;
import com.aaron212.onlinelibrarymanagement.backend.projection.UserFullProjection;
import com.aaron212.onlinelibrarymanagement.backend.projection.UserPublicProjection;
import com.aaron212.onlinelibrarymanagement.backend.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .map(user -> (UserDetails) user)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + usernameOrEmail));
    }

    public void addUser(UserRegisterDto registerRequest) throws DuplicateResourceException {
        // Check username duplication first
        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new DuplicateResourceException("User", "username", registerRequest.username());
        }
        
        // Check email duplication
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new DuplicateResourceException("User", "email", registerRequest.email());
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
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Username uniqueness
        if (userModifyDto.username()!=null && !userModifyDto.username().equals(user.getUsername()) &&
                userRepository.existsByUsernameAndIdNot(userModifyDto.username(), user.getId())) {
            throw new DuplicateResourceException("User", "username", userModifyDto.username());
        }

        // Email uniqueness
        if (userModifyDto.email()!=null && !userModifyDto.email().equals(user.getEmail()) &&
                userRepository.existsByEmailAndIdNot(userModifyDto.email(), user.getId())) {
            throw new DuplicateResourceException("User", "email", userModifyDto.email());
        }

        if(userModifyDto.username()!=null) user.setUsername(userModifyDto.username());
        if(userModifyDto.email()!=null) user.setEmail(userModifyDto.email());

        return userRepository.save(user);
    }

    public void changePassword(String name, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(name).orElseThrow(() -> new ResourceNotFoundException("User", "username", name));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BusinessLogicException("Old password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setLastUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
        userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Page<UserPublicProjection> findAllUsers(Pageable pageable) {
        return userRepository.findAllProjectedBy(pageable);
    }

    public Page<UserPublicProjection> searchUsers(String search, Pageable pageable) {
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable);
    }
}
