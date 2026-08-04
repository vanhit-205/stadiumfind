package com.VuVietAnh.stadium_booking_api.service.impl;

import com.VuVietAnh.stadium_booking_api.dto.request.CourtCreateRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.CourtResponse;
import com.VuVietAnh.stadium_booking_api.entity.Court;
import com.VuVietAnh.stadium_booking_api.entity.Stadium;
import com.VuVietAnh.stadium_booking_api.enums.CourtStatus;
import com.VuVietAnh.stadium_booking_api.exception.ResourceNotFoundException;
import com.VuVietAnh.stadium_booking_api.repository.CourtRepository;
import com.VuVietAnh.stadium_booking_api.repository.StadiumRepository;
import com.VuVietAnh.stadium_booking_api.service.CourtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourtServiceImpl implements CourtService {

    private final CourtRepository courtRepository;
    private final StadiumRepository stadiumRepository;

    @Override
    @Transactional
    public CourtResponse createCourt(CourtCreateRequest request) {
        Stadium stadium = stadiumRepository.findById(request.getStadiumId())
                .orElseThrow(() -> new ResourceNotFoundException("Stadium not found with id: " + request.getStadiumId()));

        Court court = Court.builder()
                .stadium(stadium)
                .name(request.getName())
                .sportType(request.getSportType())
                .pricePerHour(request.getPricePerHour())
                .status(request.getStatus() != null ? request.getStatus() : CourtStatus.AVAILABLE)
                .description(request.getDescription())
                .build();

        Court savedCourt = courtRepository.save(court);
        return mapToResponse(savedCourt);
    }

    @Override
    @Transactional(readOnly = true)
    public CourtResponse getCourtById(Long id) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Court not found with id: " + id));
        return mapToResponse(court);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourtResponse> getCourtsByStadium(Long stadiumId) {
        if (!stadiumRepository.existsById(stadiumId)) {
            throw new ResourceNotFoundException("Stadium not found with id: " + stadiumId);
        }
        return courtRepository.findByStadiumId(stadiumId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourtResponse> getCourtsByStadiumAndStatus(Long stadiumId, CourtStatus status, Pageable pageable) {
        return courtRepository.findByStadiumIdAndStatus(stadiumId, status, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public CourtResponse updateCourt(Long id, CourtCreateRequest request) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Court not found with id: " + id));

        court.setName(request.getName());
        court.setSportType(request.getSportType());
        court.setPricePerHour(request.getPricePerHour());
        if (request.getStatus() != null) {
            court.setStatus(request.getStatus());
        }
        court.setDescription(request.getDescription());

        Court updatedCourt = courtRepository.save(court);
        return mapToResponse(updatedCourt);
    }

    @Override
    @Transactional
    public CourtResponse updateCourtStatus(Long id, CourtStatus status) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Court not found with id: " + id));
        court.setStatus(status);
        Court updatedCourt = courtRepository.save(court);
        return mapToResponse(updatedCourt);
    }

    @Override
    @Transactional
    public void deleteCourt(Long id) {
        if (!courtRepository.existsById(id)) {
            throw new ResourceNotFoundException("Court not found with id: " + id);
        }
        courtRepository.deleteById(id);
    }

    private CourtResponse mapToResponse(Court court) {
        return CourtResponse.builder()
                .id(court.getId())
                .stadiumId(court.getStadium().getId())
                .stadiumName(court.getStadium().getName())
                .name(court.getName())
                .sportType(court.getSportType())
                .pricePerHour(court.getPricePerHour())
                .status(court.getStatus())
                .description(court.getDescription())
                .createdAt(court.getCreatedAt())
                .updatedAt(court.getUpdatedAt())
                .build();
    }
}
