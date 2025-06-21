package com.aaron212.onlinelibrarymanagement.backend.repository;

import com.aaron212.onlinelibrarymanagement.backend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, Reservation.Status status);

    List<Reservation> findByUserIdAndStatus(Long userId, Reservation.Status status);

    List<Reservation> findByBookIdAndStatusOrderByReservationTimeAsc(Long bookId, Reservation.Status status);

    Optional<Reservation> findFirstByBookIdAndStatusOrderByReservationTimeAsc(Long bookId, Reservation.Status status);
}