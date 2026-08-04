package com.VuVietAnh.stadium_booking_api.service.impl;

import com.VuVietAnh.stadium_booking_api.dto.request.FavoriteRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.FavoriteResponse;
import com.VuVietAnh.stadium_booking_api.dto.response.StadiumResponse;
import com.VuVietAnh.stadium_booking_api.entity.Favorite;
import com.VuVietAnh.stadium_booking_api.entity.Stadium;
import com.VuVietAnh.stadium_booking_api.entity.User;
import com.VuVietAnh.stadium_booking_api.exception.DuplicateResourceException;
import com.VuVietAnh.stadium_booking_api.exception.ResourceNotFoundException;
import com.VuVietAnh.stadium_booking_api.repository.FavoriteRepository;
import com.VuVietAnh.stadium_booking_api.repository.StadiumRepository;
import com.VuVietAnh.stadium_booking_api.repository.UserRepository;
import com.VuVietAnh.stadium_booking_api.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final StadiumRepository stadiumRepository;

    @Override
    @Transactional
    public FavoriteResponse addFavorite(FavoriteRequest request) {
        if (favoriteRepository.existsByUserIdAndStadiumId(request.getUserId(), request.getStadiumId())) {
            throw new DuplicateResourceException("Stadium is already in favorites");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Stadium stadium = stadiumRepository.findById(request.getStadiumId())
                .orElseThrow(() -> new ResourceNotFoundException("Stadium not found with id: " + request.getStadiumId()));

        Favorite favorite = Favorite.builder()
                .user(user)
                .stadium(stadium)
                .build();

        Favorite saved = favoriteRepository.save(favorite);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long stadiumId) {
        if (!favoriteRepository.existsByUserIdAndStadiumId(userId, stadiumId)) {
            throw new ResourceNotFoundException("Favorite record not found");
        }
        favoriteRepository.deleteByUserIdAndStadiumId(userId, stadiumId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FavoriteResponse> getFavoritesByUser(Long userId, Pageable pageable) {
        return favoriteRepository.findByUserId(userId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorite(Long userId, Long stadiumId) {
        return favoriteRepository.existsByUserIdAndStadiumId(userId, stadiumId);
    }

    private FavoriteResponse mapToResponse(Favorite favorite) {
        Stadium s = favorite.getStadium();
        StadiumResponse stadiumDto = StadiumResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .address(s.getAddress())
                .provinceCity(s.getProvinceCity())
                .district(s.getDistrict())
                .phoneNumber(s.getPhoneNumber())
                .description(s.getDescription())
                .openingTime(s.getOpeningTime())
                .closingTime(s.getClosingTime())
                .imageUrl(s.getImageUrl())
                .rating(s.getRating())
                .ownerId(s.getOwner().getId())
                .ownerName(s.getOwner().getFullName())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();

        return FavoriteResponse.builder()
                .id(favorite.getId())
                .userId(favorite.getUser().getId())
                .stadium(stadiumDto)
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
