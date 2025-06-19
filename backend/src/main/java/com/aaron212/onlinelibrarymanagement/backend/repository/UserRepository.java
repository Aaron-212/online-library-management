package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.User;
import com.aaron212.onlinelibrarymanagement.backend.projection.UserFullProjection;
import com.aaron212.onlinelibrarymanagement.backend.projection.UserPublicProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<UserPublicProjection> findPublicByUsername(String username);

    Optional<UserFullProjection> findFullByUsername(String username);

    Optional<User> findByUsername(String username);

    Optional<UserFullProjection> findFullById(Long id);

    Optional<UserFullProjection> findFullByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<UserPublicProjection> findPublicById(Long id);
}
