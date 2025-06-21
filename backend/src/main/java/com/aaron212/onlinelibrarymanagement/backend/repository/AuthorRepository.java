package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByName(String name);
    boolean existsByName(String name);
}