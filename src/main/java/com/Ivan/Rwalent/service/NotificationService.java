package com.Ivan.Rwalent.service;

import com.Ivan.Rwalent.dto.NotificationDTO;
import com.Ivan.Rwalent.model.Notification;
import com.Ivan.Rwalent.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    void createAndSendNotification(User recipient, String message, String notificationType, Long bookingId, String relatedUserName, com.Ivan.Rwalent.model.Booking.BookingStatus newBookingStatus);

    List<NotificationDTO> getNotificationsForUser(User user);

    List<NotificationDTO> getUnreadNotificationsForUser(User user);

    NotificationDTO markAsRead(User user, Long notificationId);
    
    Notification createNotification(User user, String message, String type);

    Page<Notification> getUserNotifications(User user, Pageable pageable);

    void markAllAsRead(User user);

    void deleteReadNotifications(User user);

    void deleteNotification(User user, Long notificationId);

    long getUnreadCount(User user);
} 