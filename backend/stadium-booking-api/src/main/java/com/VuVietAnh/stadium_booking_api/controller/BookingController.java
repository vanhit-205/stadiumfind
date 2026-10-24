package com.VuVietAnh.stadium_booking_api.controller;

import com.VuVietAnh.stadium_booking_api.dto.request.BookingCreateRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.ApiResponse;
import com.VuVietAnh.stadium_booking_api.dto.response.BookingResponse;
import com.VuVietAnh.stadium_booking_api.dto.response.OccupiedSlotResponse;
import com.VuVietAnh.stadium_booking_api.security.CustomUserDetails;
import com.VuVietAnh.stadium_booking_api.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller phục vụ các API liên quan đến Đặt Sân và Khung Giờ (Booking & Slots).
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * Lấy danh sách các khung giờ đã có người đặt trước trong một ngày của một sân con cụ thể.
     * Dùng để disable / làm mờ các khung giờ trên giao diện đặt sân (TimeSlotStatus.BOOKED).
     *
     * Ví dụ: GET /api/bookings/occupied-slots?courtId=1&date=2026-10-23
     */
    @GetMapping("/occupied-slots")
    public ResponseEntity<ApiResponse<List<OccupiedSlotResponse>>> getOccupiedSlots(
            @RequestParam Long courtId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<OccupiedSlotResponse> occupiedSlots = bookingService.getOccupiedSlots(courtId, date);
        return ResponseEntity.ok(ApiResponse.ok("Get occupied slots successfully", occupiedSlots));
    }

    /**
     * Tạo đơn đặt sân mới (Booking Flow - Ngày 22).
     * Yêu cầu xác thực JWT Bearer token.
     * Nếu không truyền userId trong request body, hệ thống tự động gán ID của tài khoản đang đăng nhập.
     * Kiểm tra trùng lặp giờ đặt qua JPA Query existsOverlappingBooking.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (request.getUserId() == null && userDetails != null) {
            request.setUserId(userDetails.getUser().getId());
        }
        BookingResponse booking = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Booking created successfully", booking));
    }

    /**
     * Lấy thông tin chi tiết một đơn đặt sân theo ID (chuẩn bị cho xuất vé và mã QR - Ngày 23).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(@PathVariable Long id) {
        BookingResponse booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(ApiResponse.ok("Get booking successfully", booking));
    }
}
