package com.VuVietAnh.stadium_booking_api.service;

import com.VuVietAnh.stadium_booking_api.dto.request.MatchPostCreateRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.MatchPostResponse;
import com.VuVietAnh.stadium_booking_api.enums.MatchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface MatchPostService {
    MatchPostResponse createMatchPost(MatchPostCreateRequest request);
    MatchPostResponse getMatchPostById(Long id);
    Page<MatchPostResponse> getAllOpenMatchPosts(Pageable pageable);
    Page<MatchPostResponse> getMatchPostsByCreator(Long creatorId, Pageable pageable);
    Page<MatchPostResponse> searchMatchPosts(String sportType, MatchStatus status, LocalDate fromDate, Pageable pageable);
    MatchPostResponse updateMatchPostStatus(Long id, MatchStatus status);
    void cancelMatchPost(Long id, Long creatorId);
}
