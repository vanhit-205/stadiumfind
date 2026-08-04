package com.VuVietAnh.stadium_booking_api.service.impl;

import com.VuVietAnh.stadium_booking_api.dto.request.MatchPostCreateRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.MatchPostResponse;
import com.VuVietAnh.stadium_booking_api.entity.Booking;
import com.VuVietAnh.stadium_booking_api.entity.Court;
import com.VuVietAnh.stadium_booking_api.entity.MatchPost;
import com.VuVietAnh.stadium_booking_api.entity.User;
import com.VuVietAnh.stadium_booking_api.enums.MatchStatus;
import com.VuVietAnh.stadium_booking_api.exception.BadRequestException;
import com.VuVietAnh.stadium_booking_api.exception.ResourceNotFoundException;
import com.VuVietAnh.stadium_booking_api.repository.BookingRepository;
import com.VuVietAnh.stadium_booking_api.repository.CourtRepository;
import com.VuVietAnh.stadium_booking_api.repository.MatchPostRepository;
import com.VuVietAnh.stadium_booking_api.repository.UserRepository;
import com.VuVietAnh.stadium_booking_api.service.MatchPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MatchPostServiceImpl implements MatchPostService {

    private final MatchPostRepository matchPostRepository;
    private final UserRepository userRepository;
    private final CourtRepository courtRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public MatchPostResponse createMatchPost(MatchPostCreateRequest request) {
        User creator = userRepository.findById(request.getCreatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found with id: " + request.getCreatorId()));

        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new ResourceNotFoundException("Court not found with id: " + request.getCourtId()));

        Booking booking = null;
        if (request.getBookingId() != null) {
            booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + request.getBookingId()));
        }

        MatchPost matchPost = MatchPost.builder()
                .creator(creator)
                .court(court)
                .booking(booking)
                .title(request.getTitle())
                .description(request.getDescription())
                .sportType(request.getSportType())
                .playDate(request.getPlayDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .maxParticipants(request.getMaxParticipants())
                .currentParticipants(1) // Người tạo chính là 1 thành viên
                .pricePerPerson(request.getPricePerPerson())
                .status(MatchStatus.OPEN)
                .build();

        MatchPost saved = matchPostRepository.save(matchPost);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MatchPostResponse getMatchPostById(Long id) {
        MatchPost matchPost = matchPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MatchPost not found with id: " + id));
        return mapToResponse(matchPost);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MatchPostResponse> getAllOpenMatchPosts(Pageable pageable) {
        return matchPostRepository.findByStatus(MatchStatus.OPEN, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MatchPostResponse> getMatchPostsByCreator(Long creatorId, Pageable pageable) {
        return matchPostRepository.findByCreatorId(creatorId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MatchPostResponse> searchMatchPosts(String sportType, MatchStatus status, LocalDate fromDate, Pageable pageable) {
        return matchPostRepository.searchMatchPosts(sportType, status, fromDate, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public MatchPostResponse updateMatchPostStatus(Long id, MatchStatus status) {
        MatchPost matchPost = matchPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MatchPost not found with id: " + id));
        matchPost.setStatus(status);
        MatchPost updated = matchPostRepository.save(matchPost);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void cancelMatchPost(Long id, Long creatorId) {
        MatchPost matchPost = matchPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MatchPost not found with id: " + id));

        if (!matchPost.getCreator().getId().equals(creatorId)) {
            throw new BadRequestException("You do not have permission to cancel this match post");
        }

        matchPost.setStatus(MatchStatus.CANCELLED);
        matchPostRepository.save(matchPost);
    }

    private MatchPostResponse mapToResponse(MatchPost matchPost) {
        return MatchPostResponse.builder()
                .id(matchPost.getId())
                .creatorId(matchPost.getCreator().getId())
                .creatorName(matchPost.getCreator().getFullName())
                .creatorAvatar(matchPost.getCreator().getAvatarUrl())
                .courtId(matchPost.getCourt().getId())
                .courtName(matchPost.getCourt().getName())
                .stadiumId(matchPost.getCourt().getStadium().getId())
                .stadiumName(matchPost.getCourt().getStadium().getName())
                .bookingId(matchPost.getBooking() != null ? matchPost.getBooking().getId() : null)
                .title(matchPost.getTitle())
                .description(matchPost.getDescription())
                .sportType(matchPost.getSportType())
                .playDate(matchPost.getPlayDate())
                .startTime(matchPost.getStartTime())
                .endTime(matchPost.getEndTime())
                .maxParticipants(matchPost.getMaxParticipants())
                .currentParticipants(matchPost.getCurrentParticipants())
                .pricePerPerson(matchPost.getPricePerPerson())
                .status(matchPost.getStatus())
                .createdAt(matchPost.getCreatedAt())
                .updatedAt(matchPost.getUpdatedAt())
                .build();
    }
}
