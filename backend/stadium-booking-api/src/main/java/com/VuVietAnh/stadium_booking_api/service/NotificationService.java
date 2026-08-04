package com.VuVietAnh.stadium_booking_api.service;

import com.VuVietAnh.stadium_booking_api.dto.request.NotificationCreateRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    NotificationResponse createNotification(NotificationCreateRequest request);
    Page<NotificationResponse> getNotificationsByUser(Long userId, Pageable pageable);
    Page<NotificationResponse> getUnreadNotificationsByUser(Long userId, Pageable pageable);
    long countUnreadNotifications(Long userId);
    void markAsRead(Long id);
    void markAllAsRead(Long userId);
}
