package com.VuVietAnh.stadium_booking_api.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerRevenueResponse {

    private BigDecimal totalRevenue;
    private Long totalBookings;
    private List<RevenueItemResponse> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RevenueItemResponse {
        private String period; // "2026-11-09" hoặc "2026-11"
        private BigDecimal revenue;
        private Long bookingCount;
    }
}
