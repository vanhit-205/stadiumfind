package com.VuVietAnh.stadium_booking_api.service.impl;

import com.VuVietAnh.stadium_booking_api.dto.request.StadiumCreateRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.StadiumResponse;
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
        return stadiumRepository.searchStadiums(provinceCity, keyword, pageable).map(this::mapToResponse);
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
        return StadiumResponse.builder()
                .id(stadium.getId())
                .name(stadium.getName())
                .address(stadium.getAddress())
                .provinceCity(stadium.getProvinceCity())
                .district(stadium.getDistrict())
                .phoneNumber(stadium.getPhoneNumber())
                .description(stadium.getDescription())
                .openingTime(stadium.getOpeningTime())
                .closingTime(stadium.getClosingTime())
                .imageUrl(stadium.getImageUrl())
                .rating(stadium.getRating())
                .ownerId(stadium.getOwner().getId())
                .ownerName(stadium.getOwner().getFullName())
                .createdAt(stadium.getCreatedAt())
                .updatedAt(stadium.getUpdatedAt())
                .build();
    }
}
