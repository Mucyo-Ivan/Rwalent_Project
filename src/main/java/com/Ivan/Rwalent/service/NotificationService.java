package com.Ivan.Rwalent.service;

import com.Ivan.Rwalent.dto.NotificationDTO;
import com.Ivan.Rwalent.model.User;

import java.util.List;

public interface NotificationService {

    void createAndSendNotification(User recipient, String message, String notificationType, Long bookingId, String relatedUserName, com.Ivan.Rwalent.model.Booking.BookingStatus newBookingStatus);

    List<NotificationDTO> getNotificationsForUser(User user);

    List<NotificationDTO> getUnreadNotificationsForUser(User user);

    NotificationDTO markAsRead(User user, Long notificationId);
    
    // Potentially a method to mark all as read
    // void markAllAsRead(User user);
} 