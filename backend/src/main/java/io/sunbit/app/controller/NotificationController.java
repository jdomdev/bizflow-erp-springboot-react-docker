package io.sunbit.app.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.sunbit.app.dto.NotificationDto;
import io.sunbit.app.security.dao.IUserDao;
import io.sunbit.app.security.entity.ExpenseUser;
import io.sunbit.app.service.INotificationService;

/**
 * REST Controller for notification operations.
 * All endpoints require authentication.
 */
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private INotificationService notificationService;

    @Autowired
    private IUserDao userDao;

    /**
     * Get user ID from authentication
     */
    private Long getUserIdFromAuth(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        String email = authentication.getName();
        return userDao.findByEmail(email)
            .map(ExpenseUser::getId)
            .orElse(null);
    }

    /**
     * Get all notifications for the authenticated user
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationDto>> getAllNotifications(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<NotificationDto> notifications = notificationService.getByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get only unread notifications for the authenticated user
     */
    @GetMapping("/unread")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<NotificationDto> notifications = notificationService.getUnreadByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get count of unread notifications for the authenticated user
     */
    @GetMapping("/unread/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        long count = notificationService.countUnreadByUserId(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Mark a specific notification as read
     */
    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationDto> markAsRead(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        NotificationDto notification = notificationService.markAsRead(id, userId);
        if (notification == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(notification);
    }

    /**
     * Mark all notifications as read for the authenticated user
     */
    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Integer>> markAllAsRead(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        int count = notificationService.markAllAsRead(userId);
        LOGGER.info("Marked {} notifications as read for user {}", count, userId);
        return ResponseEntity.ok(Map.of("markedAsRead", count));
    }

    /**
     * Delete a specific notification
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        boolean deleted = notificationService.delete(id, userId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        
        LOGGER.info("Deleted notification {} for user {}", id, userId);
        return ResponseEntity.noContent().build();
    }
}
