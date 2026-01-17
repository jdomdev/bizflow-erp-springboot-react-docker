package io.sunbit.app.service;

import java.util.List;

import io.sunbit.app.dto.NotificationDto;
import io.sunbit.app.entity.Notification;
import io.sunbit.app.entity.NotificationType;
import io.sunbit.app.security.entity.ExpenseUser;

public interface INotificationService {

    /**
     * Create a new notification
     */
    Notification create(ExpenseUser user, NotificationType type, String title, String message);

    /**
     * Create a notification with reference to another entity
     */
    Notification create(ExpenseUser user, NotificationType type, String title, String message, 
                        Long referenceId, String referenceType);

    /**
     * Create notifications for all users with a specific role
     */
    List<Notification> createForRole(String roleName, NotificationType type, String title, String message);

    /**
     * Create notifications for all users with a specific role, excluding a specific user
     */
    List<Notification> createForRoleExcludingUser(String roleName, Long excludeUserId, NotificationType type, String title, String message);

    /**
     * Create a notification for a specific user by ID
     */
    Notification createForUser(Long userId, NotificationType type, String title, String message);

    /**
     * Get all notifications for a user
     */
    List<NotificationDto> getByUserId(Long userId);

    /**
     * Get unread notifications for a user
     */
    List<NotificationDto> getUnreadByUserId(Long userId);

    /**
     * Count unread notifications for a user
     */
    Long countUnreadByUserId(Long userId);

    /**
     * Mark a notification as read
     */
    NotificationDto markAsRead(Long notificationId, Long userId);

    /**
     * Mark all notifications as read for a user
     */
    int markAllAsRead(Long userId);

    /**
     * Delete a notification
     */
    boolean delete(Long notificationId, Long userId);

    /**
     * Clean up old read notifications
     */
    int cleanupOldNotifications(int daysOld);

    /**
     * Convert entity to DTO
     */
    NotificationDto toDto(Notification notification);
}
