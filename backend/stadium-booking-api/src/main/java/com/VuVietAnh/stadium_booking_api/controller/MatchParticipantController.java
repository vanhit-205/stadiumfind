package com.VuVietAnh.stadium_booking_api.controller;

import com.VuVietAnh.stadium_booking_api.dto.request.MatchParticipantRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.ApiResponse;
import com.VuVietAnh.stadium_booking_api.dto.response.MatchParticipantResponse;
import com.VuVietAnh.stadium_booking_api.enums.ParticipantStatus;
import com.VuVietAnh.stadium_booking_api.security.CustomUserDetails;
import com.VuVietAnh.stadium_booking_api.service.MatchParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller phục vụ các API Tương tác Tham Gia Kèo Thể Thao (Join Match, Duyệt ứng viên, Rút lui - Ngày 25).
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MatchParticipantController {

    private final MatchParticipantService matchParticipantService;

    /**
     * Gửi yêu cầu xin tham gia vào kèo đấu (Match Post).
     * Mặc định tạo bản ghi trạng thái PENDING để chủ bài duyệt.
     */
    @PostMapping("/matches/{matchPostId}/join")
    public ResponseEntity<ApiResponse<MatchParticipantResponse>> joinMatch(
            @PathVariable Long matchPostId,
            @Valid @RequestBody MatchParticipantRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        request.setMatchPostId(matchPostId);
        if (request.getUserId() == null && userDetails != null) {
            request.setUserId(userDetails.getUser().getId());
        }
        MatchParticipantResponse response = matchParticipantService.joinMatch(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Joined match post request sent successfully", response));
    }

    /**
     * Lấy danh sách tất cả người đăng ký tham gia (ứng viên) của một kèo cụ thể.
     */
    @GetMapping("/matches/{matchPostId}/participants")
    public ResponseEntity<ApiResponse<List<MatchParticipantResponse>>> getParticipants(
            @PathVariable Long matchPostId
    ) {
        List<MatchParticipantResponse> list = matchParticipantService.getParticipantsByMatchPost(matchPostId);
        return ResponseEntity.ok(ApiResponse.ok("Get match participants successfully", list));
    }

    /**
     * Chủ kèo duyệt (ACCEPTED) hoặc từ chối (REJECTED) ứng viên xin tham gia.
     * Khi duyệt (ACCEPTED), hệ thống tự động tăng số lượng hiện tại, nếu đủ người chuyển status sang FULL.
     */
    @PutMapping("/match-participants/{participantId}/status")
    public ResponseEntity<ApiResponse<MatchParticipantResponse>> updateParticipantStatus(
            @PathVariable Long participantId,
            @RequestParam ParticipantStatus status
    ) {
        MatchParticipantResponse response = matchParticipantService.updateParticipantStatus(participantId, status);
        return ResponseEntity.ok(ApiResponse.ok("Participant status updated successfully", response));
    }

    /**
     * Rút lui khỏi kèo đấu.
     * Nếu người rút lui đã ở trạng thái ACCEPTED, hệ thống tự động trừ currentParticipants để nhường slot.
     */
    @DeleteMapping("/matches/{matchPostId}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveMatch(
            @PathVariable Long matchPostId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new AccessDeniedException("User must be authenticated to leave match");
        }
        matchParticipantService.leaveMatch(matchPostId, userDetails.getUser().getId());
        return ResponseEntity.ok(ApiResponse.ok("Left match successfully", null));
    }

    /**
     * Lấy lịch sử các kèo mà người dùng hiện tại đã đăng ký tham gia.
     */
    @GetMapping("/match-participants/my-history")
    public ResponseEntity<ApiResponse<Page<MatchParticipantResponse>>> getMyMatchHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(page = 0, size = 10, sort = "joinedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (userDetails == null) {
            throw new AccessDeniedException("User must be authenticated");
        }
        Page<MatchParticipantResponse> page = matchParticipantService.getMatchHistoryByUser(userDetails.getUser().getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok("Get match participation history successfully", page));
    }
}
