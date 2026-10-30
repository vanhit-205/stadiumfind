package com.VuVietAnh.stadium_booking_api.controller;

import com.VuVietAnh.stadium_booking_api.dto.response.ApiResponse;
import com.VuVietAnh.stadium_booking_api.dto.response.NotificationResponse;
import com.VuVietAnh.stadium_booking_api.security.CustomUserDetails;
import com.VuVietAnh.stadium_booking_api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller cung cấp các API quản lý Thông báo (Notification) cho người dùng.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Lấy danh sách tất cả thông báo của người dùng hiện tại (phân trang, mới nhất lên đầu).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getMyNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (userDetails == null) {
            throw new AccessDeniedException("User must be authenticated");
        }
        Page<NotificationResponse> page = notificationService.getNotificationsByUser(
                userDetails.getUser().getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok("Get notifications successfully", page));
    }

    /**
     * Lấy danh sách thông báo chưa đọc của người dùng hiện tại.
     */
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getUnreadNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (userDetails == null) {
            throw new AccessDeniedException("User must be authenticated");
        }
        Page<NotificationResponse> page = notificationService.getUnreadNotificationsByUser(
                userDetails.getUser().getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok("Get unread notifications successfully", page));
    }

    /**
     * Đếm số lượng thông báo chưa đọc (dùng để hiển thị badge trên icon chuông).
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new AccessDeniedException("User must be authenticated");
        }
        long count = notificationService.countUnreadNotifications(userDetails.getUser().getId());
        return ResponseEntity.ok(ApiResponse.ok("Get unread notification count successfully", count));
    }

    /**
     * Đánh dấu một thông báo cụ thể là đã đọc.
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new AccessDeniedException("User must be authenticated");
        }
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.ok("Notification marked as read", null));
    }

    /**
     * Đánh dấu tất cả thông báo của người dùng hiện tại là đã đọc.
     */
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new AccessDeniedException("User must be authenticated");
        }
        notificationService.markAllAsRead(userDetails.getUser().getId());
        return ResponseEntity.ok(ApiResponse.ok("All notifications marked as read", null));
    }
}
