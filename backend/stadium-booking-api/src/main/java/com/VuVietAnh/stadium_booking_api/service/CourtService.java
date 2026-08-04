package com.VuVietAnh.stadium_booking_api.service;

import com.VuVietAnh.stadium_booking_api.dto.request.CourtCreateRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.CourtResponse;
import com.VuVietAnh.stadium_booking_api.enums.CourtStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourtService {
    CourtResponse createCourt(CourtCreateRequest request);
    CourtResponse getCourtById(Long id);
    List<CourtResponse> getCourtsByStadium(Long stadiumId);
    Page<CourtResponse> getCourtsByStadiumAndStatus(Long stadiumId, CourtStatus status, Pageable pageable);
    CourtResponse updateCourt(Long id, CourtCreateRequest request);
    CourtResponse updateCourtStatus(Long id, CourtStatus status);
    void deleteCourt(Long id);
}
