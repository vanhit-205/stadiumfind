package com.VuVietAnh.stadium_booking_api.service;

import com.VuVietAnh.stadium_booking_api.dto.response.OwnerRevenueResponse;

public interface OwnerStatisticsService {
    OwnerRevenueResponse getRevenueStatistics(Long ownerId, Long stadiumId, String type, Integer year, Integer month);
}
