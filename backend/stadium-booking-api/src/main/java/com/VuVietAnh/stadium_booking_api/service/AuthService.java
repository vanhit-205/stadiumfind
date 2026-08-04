package com.VuVietAnh.stadium_booking_api.service;

import com.VuVietAnh.stadium_booking_api.dto.request.LoginRequest;
import com.VuVietAnh.stadium_booking_api.dto.request.RefreshTokenRequest;
import com.VuVietAnh.stadium_booking_api.dto.request.UserRegisterRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(UserRegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
}
