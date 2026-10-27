package com.VuVietAnh.stadium_booking_api.controller;

import com.VuVietAnh.stadium_booking_api.dto.request.UserChangePasswordRequest;
import com.VuVietAnh.stadium_booking_api.dto.request.UserUpdateRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.ApiResponse;
import com.VuVietAnh.stadium_booking_api.dto.response.UserResponse;
import com.VuVietAnh.stadium_booking_api.exception.UnauthorizedException;
import com.VuVietAnh.stadium_booking_api.security.CustomUserDetails;
import com.VuVietAnh.stadium_booking_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller phục vụ các API liên quan đến Hồ Sơ Người Dùng (User Profile - Ngày 24).
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Lấy thông tin cá nhân của người dùng đang đăng nhập (GET /api/users/me).
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        UserResponse response = userService.getUserById(userDetails.getUser().getId());
        return ResponseEntity.ok(ApiResponse.ok("Get profile successfully", response));
    }

    /**
     * Cập nhật thông tin cá nhân (Họ tên, SĐT, Avatar) (PUT /api/users/me).
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(
            @Valid @RequestBody UserUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        UserResponse updated = userService.updateUser(userDetails.getUser().getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", updated));
    }

    /**
     * Đổi mật khẩu cá nhân (PUT /api/users/me/change-password).
     */
    @PutMapping("/me/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody UserChangePasswordRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        userService.changePassword(userDetails.getUser().getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Password changed successfully", null));
    }
}
