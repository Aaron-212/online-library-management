package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.Notice;
import com.aaron212.onlinelibrarymanagement.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    
    Page<Notice> findByStatus(Notice.Status status, Pageable pageable);
    
    Page<Notice> findByCreatorUser(User creatorUser, Pageable pageable);
    
    @Query("SELECT n FROM Notice n WHERE n.status = :status AND (n.expireTime IS NULL OR n.expireTime > :currentTime) ORDER BY n.status DESC, n.publishTime DESC")
    Page<Notice> findActiveNoticesByStatus(@Param("status") Notice.Status status, @Param("currentTime") LocalDateTime currentTime, Pageable pageable);
    
    @Query("SELECT n FROM Notice n WHERE (n.expireTime IS NULL OR n.expireTime > :currentTime) ORDER BY n.status DESC, n.publishTime DESC")
    Page<Notice> findActiveNotices(@Param("currentTime") LocalDateTime currentTime, Pageable pageable);
    
    @Query("SELECT n FROM Notice n WHERE n.title LIKE %:keyword% OR n.content LIKE %:keyword%")
    Page<Notice> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    List<Notice> findByExpireTimeBefore(LocalDateTime expireTime);
} 
