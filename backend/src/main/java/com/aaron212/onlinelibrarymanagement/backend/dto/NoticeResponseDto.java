package com.aaron212.onlinelibrarymanagement.backend.dto;

import java.time.LocalDateTime;

public record NoticeResponseDto(
        Long id,
        String title,
        String content,
        String creatorUsername,
        LocalDateTime publishTime,
        LocalDateTime expireTime,
        Integer status,
        LocalDateTime createTime,
        LocalDateTime updateTime) {}
