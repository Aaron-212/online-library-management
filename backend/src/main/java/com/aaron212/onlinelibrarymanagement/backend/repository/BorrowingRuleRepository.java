package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.BorrowingRule;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowingRuleRepository extends JpaRepository<BorrowingRule, Long> {
    Optional<BorrowingRule> findByRuleKey(String ruleKey);

    boolean existsByRuleKey(String ruleKey);
}
