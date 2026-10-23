package com.VuVietAnh.stadium_booking_api.controller;

import com.VuVietAnh.stadium_booking_api.dto.response.ApiResponse;
import com.VuVietAnh.stadium_booking_api.dto.response.CourtResponse;
import com.VuVietAnh.stadium_booking_api.service.CourtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller phục vụ các API liên quan đến Sân con (Court).
 */
@RestController
@RequestMapping("/api/courts")
@RequiredArgsConstructor
public class CourtController {

    private final CourtService courtService;

    /**
     * Lấy danh sách sân con theo cụm sân (Stadium ID).
     * Ví dụ: GET /api/courts?stadiumId=1 hoặc GET /api/courts/stadium/1
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CourtResponse>>> getCourtsByStadiumQuery(
            @RequestParam(required = false) Long stadiumId
    ) {
        if (stadiumId != null) {
            List<CourtResponse> courts = courtService.getCourtsByStadium(stadiumId);
            return ResponseEntity.ok(ApiResponse.ok("Get courts successfully", courts));
        }
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, "stadiumId parameter is required", null));
    }

    @GetMapping("/stadium/{stadiumId}")
    public ResponseEntity<ApiResponse<List<CourtResponse>>> getCourtsByStadiumPath(
            @PathVariable Long stadiumId
    ) {
        List<CourtResponse> courts = courtService.getCourtsByStadium(stadiumId);
        return ResponseEntity.ok(ApiResponse.ok("Get courts successfully", courts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourtResponse>> getCourtById(@PathVariable Long id) {
        CourtResponse court = courtService.getCourtById(id);
        return ResponseEntity.ok(ApiResponse.ok("Get court detail successfully", court));
    }
}
