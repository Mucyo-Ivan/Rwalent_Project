package com.Ivan.Rwalent.service.impl;

import com.Ivan.Rwalent.dto.BookingDTO;
import com.Ivan.Rwalent.dto.NotificationDTO;
import com.Ivan.Rwalent.exception.BookingNotFoundException;
import com.Ivan.Rwalent.exception.InvalidBookingException;
import com.Ivan.Rwalent.model.Booking;
import com.Ivan.Rwalent.model.User;
import com.Ivan.Rwalent.repository.BookingRepository;
import com.Ivan.Rwalent.service.BookingService;
import com.Ivan.Rwalent.service.UserService;
import com.Ivan.Rwalent.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserService userService, SimpMessagingTemplate messagingTemplate, NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public BookingDTO createBooking(User user, Long talentId, BookingDTO bookingDTO) {
        User talent = userService.getUserById(talentId);
        
        if (!isTimeSlotAvailable(talentId, bookingDTO.getBookingDate(), bookingDTO.getDurationMinutes())) {
            throw new InvalidBookingException("The selected time slot is not available");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTalent(talent);
        booking.setBookingDate(bookingDTO.getBookingDate());
        booking.setDurationMinutes(bookingDTO.getDurationMinutes());
        booking.setStatus(Booking.BookingStatus.PENDING);
        booking.setNotes(bookingDTO.getNotes());
        booking.setEventLocation(bookingDTO.getEventLocation());
        booking.setAgreedPrice(bookingDTO.getAgreedPrice());
        booking.setEventRequirements(bookingDTO.getEventRequirements());

        Booking savedBooking = bookingRepository.save(booking);

        notificationService.createAndSendNotification(
            talent,
            "New booking request received from " + user.getFullName(),
            "BOOKING_REQUEST",
            savedBooking.getId(),
            user.getFullName(),
            savedBooking.getStatus()
        );

        return convertToDTO(savedBooking);
    }

    @Override
    @Transactional
    public BookingDTO updateBooking(User user, Long bookingId, BookingDTO bookingDTO) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId()) && !booking.getTalent().getId().equals(user.getId())) {
            throw new InvalidBookingException("You are not authorized to update this booking");
        }

        if (booking.getStatus() == Booking.BookingStatus.COMPLETED || 
            booking.getStatus() == Booking.BookingStatus.CANCELED) {
            throw new InvalidBookingException("Cannot update a completed or canceled booking");
        }

        booking.setBookingDate(bookingDTO.getBookingDate());
        booking.setDurationMinutes(bookingDTO.getDurationMinutes());
        booking.setNotes(bookingDTO.getNotes());
        booking.setStatus(bookingDTO.getStatus());
        booking.setEventLocation(bookingDTO.getEventLocation());
        booking.setAgreedPrice(bookingDTO.getAgreedPrice());
        booking.setEventRequirements(bookingDTO.getEventRequirements());

        Booking updatedBooking = bookingRepository.save(booking);
        return convertToDTO(updatedBooking);
    }

    @Override
    public BookingDTO getBooking(User user, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId()) && !booking.getTalent().getId().equals(user.getId())) {
            throw new InvalidBookingException("You are not authorized to view this booking");
        }

        return convertToDTO(booking);
    }

    @Override
    public List<BookingDTO> getUserBookings(User user) {
        return bookingRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getTalentBookings(User talent) {
        return bookingRepository.findByTalent(talent).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelBooking(User user, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID: " + bookingId));

        User currentUser = user;
        User userToNotify;
        String notificationMessage;
        String relatedUserNameForNotification;

        if (booking.getUser().getId().equals(currentUser.getId())) {
            userToNotify = booking.getTalent();
            notificationMessage = "Booking ID: " + booking.getId() + " with you has been canceled by " + currentUser.getFullName() + ".";
            relatedUserNameForNotification = currentUser.getFullName();
        } else if (booking.getTalent().getId().equals(currentUser.getId())) {
            userToNotify = booking.getUser();
            notificationMessage = "Your booking ID: " + booking.getId() + " with " + currentUser.getFullName() + " has been canceled by them.";
            relatedUserNameForNotification = currentUser.getFullName();
        } else {
            throw new InvalidBookingException("You are not authorized to cancel this booking");
        }

        if (booking.getStatus() == Booking.BookingStatus.COMPLETED) {
            throw new InvalidBookingException("Cannot cancel a completed booking");
        }
        if (booking.getStatus() == Booking.BookingStatus.CANCELED) {
             logger.info("Booking ID: {} is already canceled.", bookingId);
             return; 
        }

        Booking.BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(Booking.BookingStatus.CANCELED);
        bookingRepository.save(booking);
        logger.info("Booking ID: {} status changed from {} to CANCELED by user ID: {}", bookingId, oldStatus, currentUser.getId());

        notificationService.createAndSendNotification(
            userToNotify,
            notificationMessage,
            "BOOKING_CANCELED", 
            booking.getId(),
            relatedUserNameForNotification,
            Booking.BookingStatus.CANCELED
        );
    }

    @Override
    public boolean isTimeSlotAvailable(Long talentId, LocalDateTime startTime, Integer durationMinutes) {
        User talent = userService.getUserById(talentId);
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);
        
        return !bookingRepository.existsByTalentAndBookingDateBetween(
            talent,
            startTime,
            endTime
        );
    }

    @Override
    @Transactional
    public BookingDTO approveBooking(User talentUser, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID: " + bookingId));

        if (!booking.getTalent().getId().equals(talentUser.getId())) {
            throw new InvalidBookingException("You are not authorized to approve this booking.");
        }

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new InvalidBookingException("Only PENDING bookings can be approved. Current status: " + booking.getStatus());
        }

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        Booking updatedBooking = bookingRepository.save(booking);
        
        notificationService.createAndSendNotification(
            booking.getUser(),
            "Your booking request (ID: " + updatedBooking.getId() + ") with " + talentUser.getFullName() + " has been approved.",
            "BOOKING_APPROVED",
            updatedBooking.getId(),
            talentUser.getFullName(),
            updatedBooking.getStatus()
        );
        
        return convertToDTO(updatedBooking);
    }

    @Override
    @Transactional
    public BookingDTO rejectBooking(User talentUser, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID: " + bookingId));

        if (!booking.getTalent().getId().equals(talentUser.getId())) {
            throw new InvalidBookingException("You are not authorized to reject this booking.");
        }

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new InvalidBookingException("Only PENDING bookings can be rejected. Current status: " + booking.getStatus());
        }

        booking.setStatus(Booking.BookingStatus.CANCELED);
        Booking updatedBooking = bookingRepository.save(booking);
        
        notificationService.createAndSendNotification(
            booking.getUser(),
            "Unfortunately, your booking request (ID: " + updatedBooking.getId() + ") with " + talentUser.getFullName() + " was rejected.",
            "BOOKING_REJECTED",
            updatedBooking.getId(),
            talentUser.getFullName(),
            updatedBooking.getStatus()
        );
        
        return convertToDTO(updatedBooking);
    }

    @Override
    public List<BookingDTO> getPendingTalentBookings(User talentUser) {
        List<Booking> pendingBookings = bookingRepository.findByTalentAndStatus(
            talentUser, 
            Booking.BookingStatus.PENDING
        );
        
        return pendingBookings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setTalentId(booking.getTalent().getId());
        dto.setUserId(booking.getUser().getId());
        dto.setUserName(booking.getUser().getFullName());
        dto.setTalentName(booking.getTalent().getFullName());
        dto.setBookingDate(booking.getBookingDate());
        dto.setDurationMinutes(booking.getDurationMinutes());
        dto.setStatus(booking.getStatus());
        dto.setNotes(booking.getNotes());
        dto.setEventLocation(booking.getEventLocation());
        dto.setAgreedPrice(booking.getAgreedPrice());
        dto.setEventRequirements(booking.getEventRequirements());
        return dto;
    }
} 