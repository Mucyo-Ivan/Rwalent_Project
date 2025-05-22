package com.Ivan.Rwalent.dto;

import com.Ivan.Rwalent.model.Booking;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private Long id;
    private String message;
    private String notificationType;
    private Long bookingId;
    private String relatedUserName;
    private boolean isRead;
    private LocalDateTime createdAt;
    private Booking.BookingStatus newStatus;
} 