package com.Ivan.Rwalent.controller;

import com.Ivan.Rwalent.dto.NotificationDTO;
import com.Ivan.Rwalent.model.Notification;
import com.Ivan.Rwalent.model.User;
import com.Ivan.Rwalent.service.NotificationService;
import com.Ivan.Rwalent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<Page<Notification>> getUserNotifications(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = userService.findUserByEmail(authentication.getName());
        Page<Notification> notifications = notificationService.getUserNotifications(
            user, 
            PageRequest.of(page, size)
        );
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        User user = userService.findUserByEmail(authentication.getName());
        long count = notificationService.getUnreadCount(user);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{notificationId}/mark-read")
    public ResponseEntity<NotificationDTO> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication) {
        User user = userService.findUserByEmail(authentication.getName());
        NotificationDTO notification = notificationService.markAsRead(user, notificationId);
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        User user = userService.findUserByEmail(authentication.getName());
        notificationService.markAllAsRead(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}/delete")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long notificationId,
            Authentication authentication) {
        User user = userService.findUserByEmail(authentication.getName());
        notificationService.deleteNotification(user, notificationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-read")
    public ResponseEntity<Void> deleteReadNotifications(Authentication authentication) {
        User user = userService.findUserByEmail(authentication.getName());
        notificationService.deleteReadNotifications(user);
        return ResponseEntity.ok().build();
    }
} 