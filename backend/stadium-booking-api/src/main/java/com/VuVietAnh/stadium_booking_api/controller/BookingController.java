package com.VuVietAnh.stadium_booking_api.controller;

import com.VuVietAnh.stadium_booking_api.dto.response.ApiResponse;
import com.VuVietAnh.stadium_booking_api.dto.response.OccupiedSlotResponse;
import com.VuVietAnh.stadium_booking_api.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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
}
