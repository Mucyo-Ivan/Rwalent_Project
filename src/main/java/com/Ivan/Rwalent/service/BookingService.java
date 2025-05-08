package com.Ivan.Rwalent.service;

import com.Ivan.Rwalent.dto.BookingDTO;
import com.Ivan.Rwalent.model.Booking;
import com.Ivan.Rwalent.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    BookingDTO createBooking(User user, Long talentId, BookingDTO bookingDTO);
    BookingDTO updateBooking(User user, Long bookingId, BookingDTO bookingDTO);
    BookingDTO getBooking(User user, Long bookingId);
    List<BookingDTO> getUserBookings(User user);
    List<BookingDTO> getTalentBookings(User talent);
    void cancelBooking(User user, Long bookingId);
    boolean isTimeSlotAvailable(Long talentId, LocalDateTime startTime, Integer durationMinutes);

    // --- New Methods for Talent Actions ---
    BookingDTO approveBooking(User talentUser, Long bookingId);
    BookingDTO rejectBooking(User talentUser, Long bookingId);
    // --- End New Methods ---

    // --- New Method --- Gets only PENDING bookings for a talent
    List<BookingDTO> getPendingTalentBookings(User talentUser);
    // --- End New Method ---
} 