package com.example.bug_tracker.services;

import com.example.bug_tracker.config.CustomUserDetails;
import com.example.bug_tracker.entities.Notification;
import com.example.bug_tracker.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification createNotification(String userId, String bugId, String message) {
        Notification notification = Notification.builder()
                .userId(userId)
                .bugId(bugId)
                .message(message)
                .timestamp(LocalDateTime.now())
                .read(false)
                .build();
        return notificationRepository.save(notification);
    }

    public List<Notification> findNotificationsForUser() {
        String userId = getCurrentUserId();
        return notificationRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public List<Notification> findUnreadNotifications() {
        String userId = getCurrentUserId();
        return notificationRepository.findByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void markNotificationAsRead(String notificationId) {
        String userId = getCurrentUserId();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        if (!notification.getUserId().equals(userId)) {
            throw new SecurityException("User cannot mark this notification as read");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new SecurityException("User not authenticated");
        }
        return ((CustomUserDetails) authentication.getPrincipal()).getId();
    }
}

