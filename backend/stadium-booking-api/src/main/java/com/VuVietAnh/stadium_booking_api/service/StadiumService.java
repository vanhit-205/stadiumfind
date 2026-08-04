package com.VuVietAnh.stadium_booking_api.service;

import com.VuVietAnh.stadium_booking_api.dto.request.StadiumCreateRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.StadiumResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StadiumService {
    StadiumResponse createStadium(StadiumCreateRequest request);
    StadiumResponse getStadiumById(Long id);
    Page<StadiumResponse> getAllStadiums(Pageable pageable);
    Page<StadiumResponse> getStadiumsByOwner(Long ownerId, Pageable pageable);
    Page<StadiumResponse> searchStadiums(String provinceCity, String keyword, Pageable pageable);
    StadiumResponse updateStadium(Long id, StadiumCreateRequest request);
    void deleteStadium(Long id);
}
