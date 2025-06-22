package com.aaron212.onlinelibrarymanagement.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record NoticeUpdateDto(
        @NotBlank(message = "Notice title is required")
                @Size(max = 100, message = "Title must not exceed 100 characters")
                String title,
        @NotBlank(message = "Notice content is required") String content,
        @NotNull(message = "Publish time is required") LocalDateTime publishTime,
        LocalDateTime expireTime,
        @NotNull(message = "Status is required") Integer status) {}
