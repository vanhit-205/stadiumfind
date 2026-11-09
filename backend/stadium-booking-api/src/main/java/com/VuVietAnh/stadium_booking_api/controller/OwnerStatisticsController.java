package com.VuVietAnh.stadium_booking_api.controller;

import com.VuVietAnh.stadium_booking_api.dto.response.ApiResponse;
import com.VuVietAnh.stadium_booking_api.dto.response.OwnerRevenueResponse;
import com.VuVietAnh.stadium_booking_api.security.CustomUserDetails;
import com.VuVietAnh.stadium_booking_api.service.OwnerStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/owner/statistics")
@RequiredArgsConstructor
public class OwnerStatisticsController {

    private final OwnerStatisticsService ownerStatisticsService;

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('STADIUM_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OwnerRevenueResponse>> getRevenueStatistics(
            @RequestParam(required = false) Long stadiumId,
            @RequestParam(required = false, defaultValue = "DAILY") String type,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long ownerId = userDetails.getUser().getId();
        OwnerRevenueResponse response = ownerStatisticsService.getRevenueStatistics(ownerId, stadiumId, type, year, month);
        return ResponseEntity.ok(ApiResponse.ok("Get revenue statistics successfully", response));
    }
}
