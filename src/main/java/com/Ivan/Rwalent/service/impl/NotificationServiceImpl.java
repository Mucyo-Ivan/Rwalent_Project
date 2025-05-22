package com.Ivan.Rwalent.service.impl;

import com.Ivan.Rwalent.dto.NotificationDTO;
import com.Ivan.Rwalent.exception.NotificationNotFoundException;
import com.Ivan.Rwalent.exception.UnauthorizedAccessException;
import com.Ivan.Rwalent.model.Booking;
import com.Ivan.Rwalent.model.Notification;
import com.Ivan.Rwalent.model.User;
import com.Ivan.Rwalent.repository.NotificationRepository;
import com.Ivan.Rwalent.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository, SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    @Transactional
    public void createAndSendNotification(User recipient, String message, String notificationType,
                                        Long bookingId, String relatedUserName, Booking.BookingStatus newBookingStatus) {
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setMessage(message);
        notification.setNotificationType(notificationType);
        notification.setRelatedBookingId(bookingId);
        notification.setRelatedUserName(relatedUserName);
        notification.setRead(false);

        Notification savedNotification = notificationRepository.save(notification);
        logger.info("Persisted notification ID: {} for user ID: {}", savedNotification.getId(), recipient.getId());

        NotificationDTO wsNotificationDTO = convertToDTO(savedNotification);
        wsNotificationDTO.setNewStatus(newBookingStatus); 

        String userDestination = "/user/" + recipient.getId() + "/queue/notifications";
        logger.info("Sending WebSocket notification to {}: {}", userDestination, wsNotificationDTO);
        messagingTemplate.convertAndSend(userDestination, wsNotificationDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsForUser(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, Pageable.unpaged()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotificationsForUser(User user) {
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user, Pageable.unpaged()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationDTO markAsRead(User user, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with ID: " + notificationId));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to mark this notification as read.");
        }

        notification.setRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        return convertToDTO(updatedNotification);
    }

    @Override
    @Transactional
    public Notification createNotification(User user, String message, String type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setNotificationType(type);
        return notificationRepository.save(notification);
    }

    @Override
    public Page<Notification> getUserNotifications(User user, Pageable pageable) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    @Override
    @Transactional
    public void markAllAsRead(User user) {
        Page<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user, Pageable.unpaged());
        notifications.forEach(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    @Override
    @Transactional
    public void deleteReadNotifications(User user) {
        notificationRepository.deleteByUserAndIsReadTrue(user);
    }

    @Override
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Override
    @Transactional
    public void deleteNotification(User user, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with ID: " + notificationId));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to delete this notification.");
        }

        notificationRepository.delete(notification);
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setBookingId(notification.getRelatedBookingId());
        dto.setRelatedUserName(notification.getRelatedUserName());
        dto.setNotificationType(notification.getNotificationType());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
} 