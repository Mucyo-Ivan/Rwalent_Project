package com.Ivan.Rwalent.repository;

import com.Ivan.Rwalent.model.Booking;
import com.Ivan.Rwalent.model.Booking.BookingStatus;
import com.Ivan.Rwalent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByTalent(User talent);
    List<Booking> findByUser(User user);
    List<Booking> findByTalentAndBookingDateBetween(User talent, LocalDateTime start, LocalDateTime end);
    List<Booking> findByUserAndBookingDateBetween(User user, LocalDateTime start, LocalDateTime end);
    boolean existsByTalentAndBookingDateBetween(User talent, LocalDateTime start, LocalDateTime end);
    List<Booking> findByTalentAndStatus(User talent, BookingStatus status);
} 