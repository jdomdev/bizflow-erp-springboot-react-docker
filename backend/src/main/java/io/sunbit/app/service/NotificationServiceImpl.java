package io.sunbit.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.sunbit.app.dao.NotificationDao;
import io.sunbit.app.dto.NotificationDto;
import io.sunbit.app.entity.Notification;
import io.sunbit.app.entity.NotificationType;
import io.sunbit.app.security.dao.IUserDao;
import io.sunbit.app.security.entity.ExpenseUser;

@Service
@Transactional
public class NotificationServiceImpl implements INotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private IUserDao userDao;

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public Notification create(ExpenseUser user, NotificationType type, String title, String message) {
        return create(user, type, title, message, null, null);
    }

    @Override
    public Notification create(ExpenseUser user, NotificationType type, String title, String message,
                               Long referenceId, String referenceType) {
        Notification notification = Notification.create(user, type, title, message);
        if (referenceId != null) {
            notification.withReference(referenceId, referenceType);
        }
        
        notification = notificationDao.save(notification);
        
        // Send real-time notification via WebSocket if available
        sendWebSocketNotification(user.getId(), toDto(notification));
        
        LOGGER.info("Created notification for user {}: {}", user.getId(), title);
        return notification;
    }

    @Override
    public List<Notification> createForRole(String roleName, NotificationType type, String title, String message) {
        List<Notification> notifications = new ArrayList<>();
        
        // Find all users with the given role
        List<ExpenseUser> users = userDao.findByRoleName(roleName);
        
        for (ExpenseUser user : users) {
            Notification notification = create(user, type, title, message);
            notifications.add(notification);
        }
        
        LOGGER.info("Created {} notifications for role {}: {}", notifications.size(), roleName, title);
        return notifications;
    }

    @Override
    public List<Notification> createForRoleExcludingUser(String roleName, Long excludeUserId, 
                                                          NotificationType type, String title, String message) {
        List<Notification> notifications = new ArrayList<>();
        
        // Find all users with the given role, excluding specified user
        List<ExpenseUser> users = userDao.findByRoleName(roleName);
        
        for (ExpenseUser user : users) {
            // Skip the excluded user
            if (excludeUserId != null && user.getId().equals(excludeUserId)) {
                continue;
            }
            Notification notification = create(user, type, title, message);
            notifications.add(notification);
        }
        
        LOGGER.info("Created {} notifications for role {} (excluding user {}): {}", 
                    notifications.size(), roleName, excludeUserId, title);
        return notifications;
    }

    @Override
    public Notification createForUser(Long userId, NotificationType type, String title, String message) {
        ExpenseUser user = userDao.findById(userId).orElse(null);
        if (user == null) {
            LOGGER.warn("Cannot create notification: user {} not found", userId);
            return null;
        }
        return create(user, type, title, message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getByUserId(Long userId) {
        return notificationDao.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadByUserId(Long userId) {
        return notificationDao.findUnreadByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long countUnreadByUserId(Long userId) {
        return notificationDao.countUnreadByUserId(userId);
    }

    @Override
    public NotificationDto markAsRead(Long notificationId, Long userId) {
        Optional<Notification> optNotification = notificationDao.findById(notificationId);
        
        if (optNotification.isPresent()) {
            Notification notification = optNotification.get();
            
            // Security check: ensure notification belongs to user
            if (!notification.getUser().getId().equals(userId)) {
                throw new SecurityException("Cannot mark another user's notification as read");
            }
            
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            notification = notificationDao.save(notification);
            
            return toDto(notification);
        }
        
        return null;
    }

    @Override
    public int markAllAsRead(Long userId) {
        return notificationDao.markAllAsReadByUserId(userId);
    }

    @Override
    public boolean delete(Long notificationId, Long userId) {
        Optional<Notification> optNotification = notificationDao.findById(notificationId);
        
        if (optNotification.isPresent()) {
            Notification notification = optNotification.get();
            
            // Security check: ensure notification belongs to user
            if (!notification.getUser().getId().equals(userId)) {
                throw new SecurityException("Cannot delete another user's notification");
            }
            
            notificationDao.delete(notification);
            return true;
        }
        
        return false;
    }

    @Override
    public int cleanupOldNotifications(int daysOld) {
        LocalDateTime beforeDate = LocalDateTime.now().minusDays(daysOld);
        return notificationDao.deleteOldReadNotifications(beforeDate);
    }

    @Override
    public NotificationDto toDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUser().getId());
        dto.setType(notification.getType());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setReadAt(notification.getReadAt());
        dto.setReferenceId(notification.getReferenceId());
        dto.setReferenceType(notification.getReferenceType());
        return dto;
    }

    /**
     * Send notification via WebSocket to the user
     */
    private void sendWebSocketNotification(Long userId, NotificationDto notification) {
        if (messagingTemplate != null) {
            try {
                messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/notifications",
                    notification
                );
                LOGGER.debug("Sent WebSocket notification to user {}", userId);
            } catch (Exception e) {
                LOGGER.warn("Failed to send WebSocket notification: {}", e.getMessage());
            }
        }
    }
}
