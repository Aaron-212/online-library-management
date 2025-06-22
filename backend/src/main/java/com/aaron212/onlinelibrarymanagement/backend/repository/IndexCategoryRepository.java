package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.IndexCategory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexCategoryRepository extends JpaRepository<IndexCategory, Long> {
    Optional<IndexCategory> findByIndexCode(String indexCode);

    boolean existsByIndexCode(String indexCode);
}
