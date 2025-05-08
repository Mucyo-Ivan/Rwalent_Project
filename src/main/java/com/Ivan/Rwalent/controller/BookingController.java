package com.Ivan.Rwalent.controller;

import com.Ivan.Rwalent.dto.BookingDTO;
import com.Ivan.Rwalent.model.User;
import com.Ivan.Rwalent.service.BookingService;
import com.Ivan.Rwalent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    @Autowired
    public BookingController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @PostMapping("/talent/{talentId}")
    public ResponseEntity<BookingDTO> createBooking(
            Authentication authentication,
            @PathVariable Long talentId,
            @RequestBody BookingDTO bookingDTO) {
        User user = userService.getUserByEmail(authentication.getName());
        BookingDTO createdBooking = bookingService.createBooking(user, talentId, bookingDTO);
        return ResponseEntity.ok(createdBooking);
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingDTO> updateBooking(
            Authentication authentication,
            @PathVariable Long bookingId,
            @RequestBody BookingDTO bookingDTO) {
        User user = userService.getUserByEmail(authentication.getName());
        BookingDTO updatedBooking = bookingService.updateBooking(user, bookingId, bookingDTO);
        return ResponseEntity.ok(updatedBooking);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDTO> getBooking(
            Authentication authentication,
            @PathVariable Long bookingId) {
        User user = userService.getUserByEmail(authentication.getName());
        BookingDTO booking = bookingService.getBooking(user, bookingId);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/user")
    public ResponseEntity<List<BookingDTO>> getUserBookings(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        List<BookingDTO> bookings = bookingService.getUserBookings(user);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/talent")
    public ResponseEntity<List<BookingDTO>> getTalentBookings(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        List<BookingDTO> bookings = bookingService.getTalentBookings(user);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(
            Authentication authentication,
            @PathVariable Long bookingId) {
        User user = userService.getUserByEmail(authentication.getName());
        bookingService.cancelBooking(user, bookingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/talent/pending")
    public ResponseEntity<?> getMyPendingBookings(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required.");
        }
        
        User talentUser;
        try {
            talentUser = userService.getUserByEmail(authentication.getName());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found.");
        }

        if (talentUser.getUserType() != User.UserType.TALENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. User is not a talent.");
        }

        List<BookingDTO> pendingBookings = bookingService.getPendingTalentBookings(talentUser);
        return ResponseEntity.ok(pendingBookings);
    }

    @PostMapping("/{bookingId}/approve")
    public ResponseEntity<?> approveBooking(
            Authentication authentication,
            @PathVariable Long bookingId) {
        User talentUser;
        try {
            talentUser = userService.getUserByEmail(authentication.getName());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found.");
        }
         
         if (talentUser.getUserType() != User.UserType.TALENT) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. User is not a talent.");
         }
        BookingDTO updatedBooking = bookingService.approveBooking(talentUser, bookingId);
        return ResponseEntity.ok(updatedBooking);
    }

    @PostMapping("/{bookingId}/reject")
    public ResponseEntity<?> rejectBooking(
            Authentication authentication,
            @PathVariable Long bookingId) {
        User talentUser;
        try {
            talentUser = userService.getUserByEmail(authentication.getName());
         } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found.");
         }
         
         if (talentUser.getUserType() != User.UserType.TALENT) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. User is not a talent.");
         }
        BookingDTO updatedBooking = bookingService.rejectBooking(talentUser, bookingId);
        return ResponseEntity.ok(updatedBooking);
    }

    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkTimeSlotAvailability(
            @RequestParam Long talentId,
            @RequestParam String startTime,
            @RequestParam Integer durationMinutes) {
        boolean isAvailable = bookingService.isTimeSlotAvailable(
            talentId,
            java.time.LocalDateTime.parse(startTime),
            durationMinutes
        );
        return ResponseEntity.ok(isAvailable);
    }
} 