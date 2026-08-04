package com.VuVietAnh.stadium_booking_api.service;

import com.VuVietAnh.stadium_booking_api.dto.request.MatchParticipantRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.MatchParticipantResponse;
import com.VuVietAnh.stadium_booking_api.enums.ParticipantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MatchParticipantService {
    MatchParticipantResponse joinMatch(MatchParticipantRequest request);
    MatchParticipantResponse updateParticipantStatus(Long participantId, ParticipantStatus status);
    List<MatchParticipantResponse> getParticipantsByMatchPost(Long matchPostId);
    Page<MatchParticipantResponse> getMatchHistoryByUser(Long userId, Pageable pageable);
    void leaveMatch(Long matchPostId, Long userId);
}
