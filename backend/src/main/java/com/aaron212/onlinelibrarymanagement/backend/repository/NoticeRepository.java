package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.Notice;
import com.aaron212.onlinelibrarymanagement.backend.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Page<Notice> findByStatus(Notice.Status status, Pageable pageable);

    Page<Notice> findByCreatorUser(User creatorUser, Pageable pageable);

    @Query(
            "SELECT n FROM Notice n WHERE n.status = :status AND n.publishTime <= :currentTime AND (n.expireTime IS NULL OR n.expireTime > :currentTime) ORDER BY n.status DESC, n.publishTime DESC")
    Page<Notice> findActiveNoticesByStatus(
            @Param("status") Notice.Status status, @Param("currentTime") LocalDateTime currentTime, Pageable pageable);

    @Query(
            "SELECT n FROM Notice n WHERE n.publishTime <= :currentTime AND (n.expireTime IS NULL OR n.expireTime > :currentTime) ORDER BY n.status DESC, n.publishTime DESC")
    Page<Notice> findActiveNotices(@Param("currentTime") LocalDateTime currentTime, Pageable pageable);

    @Query(
            "SELECT n FROM Notice n WHERE (n.title LIKE %:keyword% OR n.content LIKE %:keyword%) AND n.publishTime <= :currentTime AND (n.expireTime IS NULL OR n.expireTime > :currentTime)")
    Page<Notice> searchByKeyword(
            @Param("keyword") String keyword, @Param("currentTime") LocalDateTime currentTime, Pageable pageable);

    @Query(
            "SELECT n FROM Notice n WHERE n.publishTime <= :currentTime AND (n.expireTime IS NULL OR n.expireTime > :currentTime)")
    Page<Notice> findPublishedNotices(@Param("currentTime") LocalDateTime currentTime, Pageable pageable);

    List<Notice> findByExpireTimeBefore(LocalDateTime expireTime);

    @Query(
            "SELECT n FROM Notice n WHERE n.id = :id AND n.publishTime <= :currentTime AND (n.expireTime IS NULL OR n.expireTime > :currentTime)")
    Optional<Notice> findPublishedNoticeById(@Param("id") Long id, @Param("currentTime") LocalDateTime currentTime);
}
