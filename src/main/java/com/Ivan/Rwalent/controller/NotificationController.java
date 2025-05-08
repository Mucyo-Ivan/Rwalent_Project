package com.Ivan.Rwalent.controller;

import com.Ivan.Rwalent.dto.NotificationDTO;
import com.Ivan.Rwalent.model.User;
import com.Ivan.Rwalent.service.NotificationService;
import com.Ivan.Rwalent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    // Get all notifications for the current user
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getMyNotifications(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        List<NotificationDTO> notifications = notificationService.getNotificationsForUser(currentUser);
        return ResponseEntity.ok(notifications);
    }

    // Get only unread notifications for the current user
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getMyUnreadNotifications(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        List<NotificationDTO> notifications = notificationService.getUnreadNotificationsForUser(currentUser);
        return ResponseEntity.ok(notifications);
    }

    // Mark a specific notification as read
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<NotificationDTO> markNotificationAsRead(Authentication authentication, @PathVariable Long notificationId) {
        User currentUser = getCurrentUser(authentication);
        NotificationDTO updatedNotification = notificationService.markAsRead(currentUser, notificationId);
        return ResponseEntity.ok(updatedNotification);
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new UsernameNotFoundException("User not authenticated"); // Or a more appropriate security exception
        }
        String userEmail = authentication.getName();
        return userService.getUserByEmail(userEmail);
    }
} 