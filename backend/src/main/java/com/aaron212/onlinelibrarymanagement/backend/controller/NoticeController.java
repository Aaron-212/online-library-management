package com.aaron212.onlinelibrarymanagement.backend.controller;

import com.aaron212.onlinelibrarymanagement.backend.dto.NoticeCreateDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.NoticeResponseDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.NoticeUpdateDto;
import com.aaron212.onlinelibrarymanagement.backend.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/notices")
@Tag(name = "Notices", description = "Notice management endpoints")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @Operation(
            summary = "Create a new notice",
            description = "Creates a new notice with the provided details",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Notice created successfully",
                        content = @Content(schema = @Schema(implementation = NoticeResponseDto.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid notice data",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createNotice(@Valid @RequestBody NoticeCreateDto noticeCreateDto, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }

        try {
            NoticeResponseDto createdNotice = noticeService.createNotice(noticeCreateDto, authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdNotice);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Failed to create notice: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get all notices", description = "Retrieves a paginated list of all notices")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Notices retrieved successfully",
                        content = @Content(schema = @Schema(implementation = Page.class)))
            })
    @GetMapping
    public ResponseEntity<Page<NoticeResponseDto>> getAllNotices(
            @Parameter(description = "Pagination parameters") @ParameterObject Pageable pageable) {
        Page<NoticeResponseDto> notices = noticeService.getAllNotices(pageable);
        return ResponseEntity.ok(notices);
    }

    @Operation(
            summary = "Get all notices for admin",
            description = "Retrieves a paginated list of all notices including unpublished ones (admin only)",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Notices retrieved successfully",
                        content = @Content(schema = @Schema(implementation = Page.class)))
            })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<Page<NoticeResponseDto>> getAllNoticesForAdmin(
            @Parameter(description = "Pagination parameters") @ParameterObject Pageable pageable) {
        Page<NoticeResponseDto> notices = noticeService.getAllNoticesForAdmin(pageable);
        return ResponseEntity.ok(notices);
    }

    @Operation(summary = "Get active notices", description = "Retrieves a paginated list of active (non-expired) notices")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Active notices retrieved successfully",
                        content = @Content(schema = @Schema(implementation = Page.class)))
            })
    @GetMapping("/active")
    public ResponseEntity<Page<NoticeResponseDto>> getActiveNotices(
            @Parameter(description = "Pagination parameters") @ParameterObject Pageable pageable) {
        Page<NoticeResponseDto> notices = noticeService.getActiveNotices(pageable);
        return ResponseEntity.ok(notices);
    }

    @Operation(summary = "Get notices by status", description = "Retrieves active notices filtered by status (1=SHOW, 2=PINNED)")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Notices retrieved successfully",
                        content = @Content(schema = @Schema(implementation = Page.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid status value",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getNoticesByStatus(
            @Parameter(description = "Notice status (1=SHOW, 2=PINNED)", required = true, example = "1") 
            @PathVariable @Positive Integer status,
            @Parameter(description = "Pagination parameters") @ParameterObject Pageable pageable) {
        try {
            Page<NoticeResponseDto> notices = noticeService.getNoticesByStatus(status, pageable);
            return ResponseEntity.ok(notices);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get notice by ID", description = "Retrieves a specific notice by its ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Notice found",
                        content = @Content(schema = @Schema(implementation = NoticeResponseDto.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Notice not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<?> getNoticeById(
            @Parameter(description = "Notice ID", required = true, example = "1") @PathVariable @Positive Long id,
            Authentication authentication) {
        try {
            // Get username if authenticated, otherwise use null (will be treated as non-admin)
            String username = (authentication != null && authentication.isAuthenticated()) 
                ? authentication.getName() 
                : null;
            
            Optional<NoticeResponseDto> notice = noticeService.getNoticeById(id, username);
            if (notice.isPresent()) {
                return ResponseEntity.ok(notice.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Notice not found"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Notice not found"));
        }
    }

    @Operation(
            summary = "Get notices by creator",
            description = "Retrieves notices created by a specific user",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Notices retrieved successfully",
                        content = @Content(schema = @Schema(implementation = Page.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Creator not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/creator/{username}")
    public ResponseEntity<?> getNoticesByCreator(
            @Parameter(description = "Creator username", required = true, example = "admin") 
            @PathVariable @NotBlank String username,
            @Parameter(description = "Pagination parameters") @ParameterObject Pageable pageable) {
        try {
            Page<NoticeResponseDto> notices = noticeService.getNoticesByCreator(username, pageable);
            return ResponseEntity.ok(notices);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Search notices", description = "Searches for notices by keyword in title or content")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Search completed successfully",
                        content = @Content(schema = @Schema(implementation = Page.class)))
            })
    @GetMapping("/search")
    public ResponseEntity<Page<NoticeResponseDto>> searchNotices(
            @Parameter(description = "Search keyword", required = true, example = "library hours")
            @RequestParam @NotBlank String keyword,
            @Parameter(description = "Pagination parameters") @ParameterObject Pageable pageable) {
        Page<NoticeResponseDto> notices = noticeService.searchNotices(keyword, pageable);
        return ResponseEntity.ok(notices);
    }

    @Operation(
            summary = "Update notice",
            description = "Updates an existing notice with the provided details",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Notice updated successfully",
                        content = @Content(schema = @Schema(implementation = NoticeResponseDto.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid notice data",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "User not authorized to update this notice",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Notice not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNotice(
            @Parameter(description = "Notice ID", required = true, example = "1") @PathVariable @Positive Long id,
            @Valid @RequestBody NoticeUpdateDto noticeUpdateDto,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }

        try {
            NoticeResponseDto updatedNotice = noticeService.updateNotice(id, noticeUpdateDto, authentication.getName());
            return ResponseEntity.ok(updatedNotice);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            } else if (e.getMessage().contains("only update your own") || e.getMessage().contains("must be an admin")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Failed to update notice: " + e.getMessage()));
            }
        }
    }

    @Operation(
            summary = "Delete notice",
            description = "Deletes a notice by its ID",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Notice deleted successfully"),
                @ApiResponse(
                        responseCode = "401",
                        description = "User not authenticated",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "User not authorized to delete this notice",
                        content = @Content(schema = @Schema(implementation = Map.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Notice not found",
                        content = @Content(schema = @Schema(implementation = Map.class)))
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotice(
            @Parameter(description = "Notice ID", required = true, example = "1") @PathVariable @Positive Long id,
            Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not authenticated"));
        }

        try {
            noticeService.deleteNotice(id, authentication.getName());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            } else if (e.getMessage().contains("only delete your own") || e.getMessage().contains("must be an admin")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Failed to delete notice"));
            }
        }
    }
} 
