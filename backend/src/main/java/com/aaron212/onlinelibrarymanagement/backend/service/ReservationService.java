package com.aaron212.onlinelibrarymanagement.backend.service;

import com.aaron212.onlinelibrarymanagement.backend.exception.BusinessLogicException;
import com.aaron212.onlinelibrarymanagement.backend.exception.ResourceNotFoundException;
import com.aaron212.onlinelibrarymanagement.backend.model.Book;
import com.aaron212.onlinelibrarymanagement.backend.model.BookCopy;
import com.aaron212.onlinelibrarymanagement.backend.model.Borrow;
import com.aaron212.onlinelibrarymanagement.backend.model.Reservation;
import com.aaron212.onlinelibrarymanagement.backend.model.User;
import com.aaron212.onlinelibrarymanagement.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BorrowRepository borrowRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              BookRepository bookRepository,
                              BookCopyRepository bookCopyRepository,
                              BorrowRepository borrowRepository,
                              UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.borrowRepository = borrowRepository;
        this.userRepository = userRepository;
    }

    /**
     * Reserve a book for user when all copies are currently borrowed.
     */
    public Reservation createReservation(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));

        // Step 1: if book has available copies -> ask to borrow instead
        long availableCopies = bookCopyRepository.countByBookAndStatus(book, BookCopy.Status.AVAILABLE);
        if (availableCopies > 0) {
            throw new BusinessLogicException("图书目前可借，无需预约");
        }

        // Step 2: check user overdue borrows
        if (borrowRepository.existsByUserIdAndStatus(userId, Borrow.Status.OVERDUE)) {
            throw new BusinessLogicException("您有逾期未处理记录，暂无法预约");
        }

        // Step 3: check existing active reservation for same book
        if (reservationRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, Reservation.Status.WAITING)) {
            throw new BusinessLogicException("您已预约此书，请勿重复预约");
        }

        // Step 4: create reservation record
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationTime(LocalDateTime.now());
        reservation.setStatus(Reservation.Status.WAITING);
        return reservationRepository.save(reservation);
    }

    public void cancelReservation(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", reservationId));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new BusinessLogicException("只能取消自己的预约");
        }
        if (reservation.getStatus() != Reservation.Status.WAITING) {
            throw new BusinessLogicException("无法取消已完成或已取消的预约");
        }
        reservation.setStatus(Reservation.Status.CANCELLED);
    }

    public List<Reservation> getUserActiveReservations(Long userId) {
        return reservationRepository.findByUserIdAndStatus(userId, Reservation.Status.WAITING);
    }

    /**
     * When a borrowed copy is returned, call this to notify next user in queue.
     */
    public void processNextReservation(Long bookId) {
        reservationRepository.findFirstByBookIdAndStatusOrderByReservationTimeAsc(bookId, Reservation.Status.WAITING)
                .ifPresent(reservation -> {
                    reservation.setStatus(Reservation.Status.EXPIRED); // Here we could set NOTIFIED instead; simplified
                    reservation.setNoticeTime(LocalDateTime.now());
                });
    }
}