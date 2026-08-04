package com.VuVietAnh.stadium_booking_api.service.impl;

import com.VuVietAnh.stadium_booking_api.dto.request.NotificationCreateRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.NotificationResponse;
import com.VuVietAnh.stadium_booking_api.entity.Notification;
import com.VuVietAnh.stadium_booking_api.entity.User;
import com.VuVietAnh.stadium_booking_api.enums.NotificationType;
import com.VuVietAnh.stadium_booking_api.exception.ResourceNotFoundException;
import com.VuVietAnh.stadium_booking_api.repository.NotificationRepository;
import com.VuVietAnh.stadium_booking_api.repository.UserRepository;
import com.VuVietAnh.stadium_booking_api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public NotificationResponse createNotification(NotificationCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Notification notification = Notification.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .type(request.getType() != null ? request.getType() : NotificationType.SYSTEM)
                .isRead(false)
                .referenceId(request.getReferenceId())
                .build();

        Notification saved = notificationRepository.save(notification);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotificationsByUser(Long userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUnreadNotificationsByUser(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadNotifications(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId, Pageable.unpaged()).getContent();
        unreadNotifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .referenceId(notification.getReferenceId())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
