package com.VuVietAnh.stadium_booking_api.service;

import com.VuVietAnh.stadium_booking_api.dto.request.UserChangePasswordRequest;
import com.VuVietAnh.stadium_booking_api.dto.request.UserRegisterRequest;
import com.VuVietAnh.stadium_booking_api.dto.request.UserUpdateRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.UserResponse;
import com.VuVietAnh.stadium_booking_api.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse registerUser(UserRegisterRequest request);
    UserResponse getUserById(Long id);
    UserResponse getUserByUsername(String username);
    Page<UserResponse> getAllUsers(Pageable pageable);
    Page<UserResponse> getUsersByRole(Role role, Pageable pageable);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    void changePassword(Long id, UserChangePasswordRequest request);
    void toggleUserStatus(Long id);
}
