package com.VuVietAnh.stadium_booking_api.controller;

import com.VuVietAnh.stadium_booking_api.dto.request.MatchPostCreateRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.ApiResponse;
import com.VuVietAnh.stadium_booking_api.dto.response.MatchPostResponse;
import com.VuVietAnh.stadium_booking_api.enums.MatchStatus;
import com.VuVietAnh.stadium_booking_api.security.CustomUserDetails;
import com.VuVietAnh.stadium_booking_api.service.MatchPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller phục vụ các API Quản lý Bài Đăng Tìm Kèo Thể Thao (Match Posts - Ngày 25).
 */
@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchPostController {

    private final MatchPostService matchPostService;

    /**
     * Tạo bài đăng tìm kèo mới.
     * Tự động lấy userId từ token JWT nếu request không truyền creatorId.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MatchPostResponse>> createMatchPost(
            @Valid @RequestBody MatchPostCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (request.getCreatorId() == null && userDetails != null) {
            request.setCreatorId(userDetails.getUser().getId());
        }
        MatchPostResponse response = matchPostService.createMatchPost(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Match post created successfully", response));
    }

    /**
     * Lấy danh sách bài đăng tìm kèo có hỗ trợ tìm kiếm và phân trang (News Feed).
     * Cho phép lọc theo môn thể thao (sportType), trạng thái (status: OPEN, FULL, etc.), và ngày thi đấu (fromDate).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MatchPostResponse>>> getMatchPosts(
            @RequestParam(required = false) String sportType,
            @RequestParam(required = false) MatchStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<MatchPostResponse> posts = matchPostService.searchMatchPosts(sportType, status, fromDate, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Get match posts successfully", posts));
    }

    /**
     * Xem chi tiết bài đăng tìm kèo theo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MatchPostResponse>> getMatchPostById(@PathVariable Long id) {
        MatchPostResponse response = matchPostService.getMatchPostById(id);
        return ResponseEntity.ok(ApiResponse.ok("Get match post detail successfully", response));
    }

    /**
     * Lấy danh sách các bài đăng do chính người dùng hiện tại tạo.
     */
    @GetMapping("/my-posts")
    public ResponseEntity<ApiResponse<Page<MatchPostResponse>>> getMyMatchPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (userDetails == null) {
            throw new AccessDeniedException("User must be authenticated");
        }
        Long creatorId = userDetails.getUser().getId();
        Page<MatchPostResponse> posts = matchPostService.getMatchPostsByCreator(creatorId, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Get my match posts successfully", posts));
    }

    /**
     * Cập nhật trạng thái bài đăng tìm kèo (OPEN, FULL, FINISHED, CANCELLED).
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<MatchPostResponse>> updateMatchPostStatus(
            @PathVariable Long id,
            @RequestParam MatchStatus status
    ) {
        MatchPostResponse response = matchPostService.updateMatchPostStatus(id, status);
        return ResponseEntity.ok(ApiResponse.ok("Match post status updated successfully", response));
    }

    /**
     * Hủy bài đăng tìm kèo (Chỉ người tạo mới có quyền hủy).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelMatchPost(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new AccessDeniedException("User must be authenticated to cancel post");
        }
        matchPostService.cancelMatchPost(id, userDetails.getUser().getId());
        return ResponseEntity.ok(ApiResponse.ok("Match post cancelled successfully", null));
    }
}
