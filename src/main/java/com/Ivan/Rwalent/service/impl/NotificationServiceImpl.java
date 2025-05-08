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
        notification.setRecipient(recipient);
        notification.setMessage(message);
        notification.setNotificationType(notificationType);
        notification.setRelatedBookingId(bookingId);
        notification.setRelatedUserName(relatedUserName);
        notification.setRead(false); // Explicitly set, though default

        Notification savedNotification = notificationRepository.save(notification);
        logger.info("Persisted notification ID: {} for user ID: {}", savedNotification.getId(), recipient.getId());

        // Prepare DTO for WebSocket
        NotificationDTO wsNotificationDTO = convertToDTO(savedNotification);
        // The 'newStatus' field in NotificationDTO might be redundant if the 'message' or 'notificationType' already conveys this.
        // However, we'll keep it for consistency if the client expects it based on previous DTO structure.
        wsNotificationDTO.setNewStatus(newBookingStatus); 

        String userDestination = "/user/" + recipient.getId() + "/queue/notifications";
        logger.info("Sending WebSocket notification to {}: {}", userDestination, wsNotificationDTO);
        messagingTemplate.convertAndSend(userDestination, wsNotificationDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsForUser(User user) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotificationsForUser(User user) {
        return notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationDTO markAsRead(User user, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with ID: " + notificationId));

        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to mark this notification as read.");
        }

        notification.setRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        return convertToDTO(updatedNotification);
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setBookingId(notification.getRelatedBookingId());
        // newStatus is not directly on Notification entity; set from parameters if needed when creating, or handle on client
        dto.setRelatedUserName(notification.getRelatedUserName());
        dto.setNotificationType(notification.getNotificationType());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
} 