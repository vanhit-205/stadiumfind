package com.VuVietAnh.stadium_booking_api.service.impl;

import com.VuVietAnh.stadium_booking_api.dto.request.StadiumCreateRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.StadiumResponse;
import com.VuVietAnh.stadium_booking_api.entity.Court;
import com.VuVietAnh.stadium_booking_api.entity.Stadium;
import com.VuVietAnh.stadium_booking_api.entity.User;
import com.VuVietAnh.stadium_booking_api.enums.Role;
import com.VuVietAnh.stadium_booking_api.exception.BadRequestException;
import com.VuVietAnh.stadium_booking_api.exception.ResourceNotFoundException;
import com.VuVietAnh.stadium_booking_api.repository.StadiumRepository;
import com.VuVietAnh.stadium_booking_api.repository.UserRepository;
import com.VuVietAnh.stadium_booking_api.service.StadiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StadiumServiceImpl implements StadiumService {

    private final StadiumRepository stadiumRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public StadiumResponse createStadium(StadiumCreateRequest request) {
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + request.getOwnerId()));

        if (owner.getRole() != Role.STADIUM_OWNER && owner.getRole() != Role.ADMIN) {
            throw new BadRequestException("User does not have permission to own a stadium");
        }

        Stadium stadium = Stadium.builder()
                .owner(owner)
                .name(request.getName())
                .address(request.getAddress())
                .provinceCity(request.getProvinceCity())
                .district(request.getDistrict())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .phoneNumber(request.getPhoneNumber())
                .description(request.getDescription())
                .openingTime(request.getOpeningTime())
                .closingTime(request.getClosingTime())
                .imageUrl(request.getImageUrl())
                .rating(5.0)
                .build();

        Stadium savedStadium = stadiumRepository.save(stadium);
        return mapToResponse(savedStadium);
    }

    @Override
    @Transactional(readOnly = true)
    public StadiumResponse getStadiumById(Long id) {
        Stadium stadium = stadiumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stadium not found with id: " + id));
        return mapToResponse(stadium);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StadiumResponse> getAllStadiums(Pageable pageable) {
        return stadiumRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StadiumResponse> getStadiumsByOwner(Long ownerId, Pageable pageable) {
        return stadiumRepository.findByOwnerId(ownerId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StadiumResponse> searchStadiums(String provinceCity, String keyword, Pageable pageable) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanProvince = (provinceCity != null && !provinceCity.trim().isEmpty()) ? provinceCity.trim() : null;
        return stadiumRepository.searchStadiums(cleanProvince, cleanKeyword, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StadiumResponse> getNearbyStadiums(Double latitude, Double longitude, Double radiusKm) {
        if (latitude == null || longitude == null) {
            return Collections.emptyList();
        }

        double radius = (radiusKm != null && radiusKm > 0) ? radiusKm : 20.0;

        // 1 độ vĩ độ ~ 111 km
        double latDelta = radius / 111.0;
        double minLat = latitude - latDelta;
        double maxLat = latitude + latDelta;

        // 1 độ kinh độ ~ 111 * cos(rad(lat)) km
        double cosLat = Math.cos(Math.toRadians(latitude));
        double lonDelta = (cosLat > 0.0001) ? (radius / (111.0 * cosLat)) : latDelta;
        double minLng = longitude - lonDelta;
        double maxLng = longitude + lonDelta;

        List<Stadium> stadiums = stadiumRepository.findStadiumsInBoundingBox(minLat, maxLat, minLng, maxLng);

        // Fallback: nếu trong bán kính bounding box chưa có sân (ví dụ emulator đặt tọa độ ở vị trí khác),
        // lấy danh sách tất cả sân có tọa độ để bản đồ luôn có dữ liệu hiển thị mượt mà
        if (stadiums.isEmpty()) {
            stadiums = stadiumRepository.findAllWithCoordinates();
        }

        return stadiums.stream()
                .map(stadium -> {
                    double dist = calculateHaversineDistance(latitude, longitude, stadium.getLatitude(), stadium.getLongitude());
                    StadiumResponse response = mapToResponse(stadium);
                    response.setDistance(dist);
                    return response;
                })
                .sorted(Comparator.comparing(StadiumResponse::getDistance))
                .toList();
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371.0; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;
        return Math.round(distance * 10.0) / 10.0; // làm tròn 1 chữ số thập phân
    }

    @Override
    @Transactional
    public StadiumResponse updateStadium(Long id, StadiumCreateRequest request) {
        Stadium stadium = stadiumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stadium not found with id: " + id));

        stadium.setName(request.getName());
        stadium.setAddress(request.getAddress());
        stadium.setProvinceCity(request.getProvinceCity());
        stadium.setDistrict(request.getDistrict());
        stadium.setPhoneNumber(request.getPhoneNumber());
        stadium.setDescription(request.getDescription());
        stadium.setOpeningTime(request.getOpeningTime());
        stadium.setClosingTime(request.getClosingTime());
        if (request.getImageUrl() != null) {
            stadium.setImageUrl(request.getImageUrl());
        }

        Stadium updatedStadium = stadiumRepository.save(stadium);
        return mapToResponse(updatedStadium);
    }

    @Override
    @Transactional
    public void deleteStadium(Long id) {
        if (!stadiumRepository.existsById(id)) {
            throw new ResourceNotFoundException("Stadium not found with id: " + id);
        }
        stadiumRepository.deleteById(id);
    }

    private StadiumResponse mapToResponse(Stadium stadium) {
        BigDecimal minPrice = null;
        int totalCourts = 0;
        List<String> sportTypes = Collections.emptyList();

        List<Court> courts = stadium.getCourts();
        if (courts != null && !courts.isEmpty()) {
            totalCourts = courts.size();
            minPrice = courts.stream()
                    .map(Court::getPricePerHour)
                    .filter(Objects::nonNull)
                    .min(BigDecimal::compareTo)
                    .orElse(null);
            sportTypes = courts.stream()
                    .map(Court::getSportType)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
        }

        Long ownerId = stadium.getOwner() != null ? stadium.getOwner().getId() : null;
        String ownerName = stadium.getOwner() != null ? stadium.getOwner().getFullName() : null;

        return StadiumResponse.builder()
                .id(stadium.getId())
                .name(stadium.getName())
                .address(stadium.getAddress())
                .provinceCity(stadium.getProvinceCity())
                .district(stadium.getDistrict())
                .latitude(stadium.getLatitude())
                .longitude(stadium.getLongitude())
                .phoneNumber(stadium.getPhoneNumber())
                .description(stadium.getDescription())
                .openingTime(stadium.getOpeningTime())
                .closingTime(stadium.getClosingTime())
                .imageUrl(stadium.getImageUrl())
                .rating(stadium.getRating())
                .minPricePerHour(minPrice)
                .totalCourts(totalCourts)
                .sportTypes(sportTypes)
                .ownerId(ownerId)
                .ownerName(ownerName)
                .createdAt(stadium.getCreatedAt())
                .updatedAt(stadium.getUpdatedAt())
                .build();
    }
}
