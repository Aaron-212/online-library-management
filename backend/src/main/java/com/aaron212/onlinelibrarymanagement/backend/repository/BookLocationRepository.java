package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.BookLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookLocationRepository extends JpaRepository<BookLocation, Long> {}
