package com.Ivan.Rwalent.dto;

import com.Ivan.Rwalent.model.Booking.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id; // Added for identifying the notification
    private String message;
    private Long bookingId;
    private BookingStatus newStatus; // Kept for consistency if WebSocket DTO is reused
    private String relatedUserName;
    private String notificationType;
    private boolean isRead; // Added
    private LocalDateTime createdAt; // Added
} 