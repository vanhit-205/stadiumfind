package com.VuVietAnh.stadium_booking_api.service;

import com.VuVietAnh.stadium_booking_api.dto.request.FavoriteRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.FavoriteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteService {
    FavoriteResponse addFavorite(FavoriteRequest request);
    void removeFavorite(Long userId, Long stadiumId);
    Page<FavoriteResponse> getFavoritesByUser(Long userId, Pageable pageable);
    boolean isFavorite(Long userId, Long stadiumId);
}
