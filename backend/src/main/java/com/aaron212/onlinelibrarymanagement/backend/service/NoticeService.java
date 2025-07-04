package com.aaron212.onlinelibrarymanagement.backend.service;

import com.aaron212.onlinelibrarymanagement.backend.dto.NoticeCreateDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.NoticeResponseDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.NoticeUpdateDto;
import com.aaron212.onlinelibrarymanagement.backend.model.Notice;
import com.aaron212.onlinelibrarymanagement.backend.model.User;
import com.aaron212.onlinelibrarymanagement.backend.repository.NoticeRepository;
import com.aaron212.onlinelibrarymanagement.backend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    public NoticeService(NoticeRepository noticeRepository, UserRepository userRepository) {
        this.noticeRepository = noticeRepository;
        this.userRepository = userRepository;
    }

    public NoticeResponseDto createNotice(NoticeCreateDto noticeCreateDto, String creatorUsername) {
        User creatorUser = userRepository
                .findByUsername(creatorUsername)
                .orElseThrow(() -> new RuntimeException("Creator user not found"));

        Notice.Status status = Notice.Status.fromValue(noticeCreateDto.status());

        Notice notice = new Notice();
        notice.setTitle(noticeCreateDto.title());
        notice.setContent(noticeCreateDto.content());
        notice.setCreatorUser(creatorUser);
        notice.setPublishTime(noticeCreateDto.publishTime());
        notice.setExpireTime(noticeCreateDto.expireTime());
        notice.setStatus(status);

        Notice savedNotice = noticeRepository.save(notice);
        return mapToResponseDto(savedNotice);
    }

    @Transactional(readOnly = true)
    public Page<NoticeResponseDto> getAllNotices(Pageable pageable) {
        // For getAllNotices, we should only return published notices
        LocalDateTime currentTime = LocalDateTime.now();
        return noticeRepository.findPublishedNotices(currentTime, pageable).map(this::mapToResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<NoticeResponseDto> getAllNoticesForAdmin(Pageable pageable) {
        // For admin, return all notices including unpublished ones
        return noticeRepository.findAll(pageable).map(this::mapToResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<NoticeResponseDto> getActiveNotices(Pageable pageable) {
        LocalDateTime currentTime = LocalDateTime.now();
        return noticeRepository.findActiveNotices(currentTime, pageable).map(this::mapToResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<NoticeResponseDto> getNoticesByStatus(Integer statusValue, Pageable pageable) {
        Notice.Status status = Notice.Status.fromValue(statusValue);
        LocalDateTime currentTime = LocalDateTime.now();
        return noticeRepository
                .findActiveNoticesByStatus(status, currentTime, pageable)
                .map(this::mapToResponseDto);
    }

    @Transactional(readOnly = true)
    public Optional<NoticeResponseDto> getNoticeById(Long id, String username) {
        boolean isAdmin = false;
        if (username != null) {
            isAdmin = userRepository
                    .findByUsername(username)
                    .map(user -> user.getRole() == User.Role.ADMIN)
                    .orElse(false);
        }

        Optional<Notice> noticeOpt;
        if (isAdmin) {
            noticeOpt = noticeRepository.findById(id);
        } else {
            LocalDateTime currentTime = LocalDateTime.now();
            noticeOpt = noticeRepository.findPublishedNoticeById(id, currentTime);
        }

        return noticeOpt.map(this::mapToResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<NoticeResponseDto> getNoticesByCreator(String creatorUsername, Pageable pageable) {
        User creatorUser = userRepository
                .findByUsername(creatorUsername)
                .orElseThrow(() -> new RuntimeException("Creator user not found"));
        return noticeRepository.findByCreatorUser(creatorUser, pageable).map(this::mapToResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<NoticeResponseDto> searchNotices(String keyword, Pageable pageable) {
        LocalDateTime currentTime = LocalDateTime.now();
        return noticeRepository.searchByKeyword(keyword, currentTime, pageable).map(this::mapToResponseDto);
    }

    public NoticeResponseDto updateNotice(Long id, NoticeUpdateDto noticeUpdateDto, String updaterUsername) {
        Notice notice = noticeRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found with id: " + id));

        // Check if the updater is the creator or has admin role
        User updaterUser = userRepository
                .findByUsername(updaterUsername)
                .orElseThrow(() -> new RuntimeException("Updater user not found"));

        if (!notice.getCreatorUser().getId().equals(updaterUser.getId())
                && !updaterUser.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("You can only update your own notices or you must be an admin");
        }

        Notice.Status status = Notice.Status.fromValue(noticeUpdateDto.status());

        notice.setTitle(noticeUpdateDto.title());
        notice.setContent(noticeUpdateDto.content());
        notice.setPublishTime(noticeUpdateDto.publishTime());
        notice.setExpireTime(noticeUpdateDto.expireTime());
        notice.setStatus(status);

        Notice savedNotice = noticeRepository.save(notice);
        return mapToResponseDto(savedNotice);
    }

    public void deleteNotice(Long id, String deleterUsername) {
        Notice notice = noticeRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found with id: " + id));

        // Check if the deleter is the creator or has admin role
        User deleterUser = userRepository
                .findByUsername(deleterUsername)
                .orElseThrow(() -> new RuntimeException("Deleter user not found"));

        if (!notice.getCreatorUser().getId().equals(deleterUser.getId())
                && !deleterUser.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("You can only delete your own notices or you must be an admin");
        }

        noticeRepository.delete(notice);
    }

    private NoticeResponseDto mapToResponseDto(Notice notice) {
        return new NoticeResponseDto(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatorUser().getUsername(),
                notice.getPublishTime(),
                notice.getExpireTime(),
                notice.getStatus().getValue(),
                notice.getCreateTime(),
                notice.getUpdateTime());
    }
}
