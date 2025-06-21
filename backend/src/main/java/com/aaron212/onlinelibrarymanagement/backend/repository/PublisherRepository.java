package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    Optional<Publisher> findByName(String name);
    boolean existsByName(String name);
}