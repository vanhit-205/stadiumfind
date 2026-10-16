package com.VuVietAnh.stadium_booking_api.controller;

import com.VuVietAnh.stadium_booking_api.dto.request.StadiumCreateRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.ApiResponse;
import com.VuVietAnh.stadium_booking_api.dto.response.StadiumResponse;
import com.VuVietAnh.stadium_booking_api.service.StadiumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stadiums")
@RequiredArgsConstructor
public class StadiumController {

    private final StadiumService stadiumService;

    /**
     * Lấy danh sách sân bóng hỗ trợ phân trang, tìm kiếm theo từ khóa và lọc theo tỉnh/thành phố.
     * Ví dụ:
     * - GET /api/stadiums?page=0&size=10
     * - GET /api/stadiums?keyword=Sân bóng&provinceCity=Hà Nội
     * - GET /api/stadiums?sort=rating,desc
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<StadiumResponse>>> getStadiums(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String provinceCity,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<StadiumResponse> page;
        if ((keyword != null && !keyword.trim().isEmpty()) || (provinceCity != null && !provinceCity.trim().isEmpty())) {
            page = stadiumService.searchStadiums(provinceCity, keyword, pageable);
        } else {
            page = stadiumService.getAllStadiums(pageable);
        }
        return ResponseEntity.ok(ApiResponse.ok("Get stadiums successfully", page));
    }

    /**
     * Lấy danh sách sân bóng gần vị trí hiện tại (tính khoảng cách bằng công thức Haversine).
     * Ví dụ: GET /api/stadiums/nearby?latitude=21.0285&longitude=105.8542&radiusKm=20.0
     */
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<java.util.List<StadiumResponse>>> getNearbyStadiums(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false, defaultValue = "20.0") Double radiusKm
    ) {
        java.util.List<StadiumResponse> list = stadiumService.getNearbyStadiums(latitude, longitude, radiusKm);
        return ResponseEntity.ok(ApiResponse.ok("Get nearby stadiums successfully", list));
    }

    /**
     * Lấy thông tin chi tiết một sân bóng theo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StadiumResponse>> getStadiumById(@PathVariable Long id) {
        StadiumResponse data = stadiumService.getStadiumById(id);
        return ResponseEntity.ok(ApiResponse.ok("Get stadium detail successfully", data));
    }

    /**
     * Lấy danh sách sân bóng của một chủ sân (Owner) cụ thể (có phân trang).
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<ApiResponse<Page<StadiumResponse>>> getStadiumsByOwner(
            @PathVariable Long ownerId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<StadiumResponse> page = stadiumService.getStadiumsByOwner(ownerId, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Get stadiums by owner successfully", page));
    }

    /**
     * Tạo mới sân bóng (yêu cầu quyền STADIUM_OWNER hoặc ADMIN).
     */
    @PostMapping
    public ResponseEntity<ApiResponse<StadiumResponse>> createStadium(@Valid @RequestBody StadiumCreateRequest request) {
        StadiumResponse data = stadiumService.createStadium(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Create stadium successfully", data));
    }

    /**
     * Cập nhật thông tin sân bóng theo ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StadiumResponse>> updateStadium(
            @PathVariable Long id,
            @Valid @RequestBody StadiumCreateRequest request
    ) {
        StadiumResponse data = stadiumService.updateStadium(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Update stadium successfully", data));
    }

    /**
     * Xóa sân bóng theo ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStadium(@PathVariable Long id) {
        stadiumService.deleteStadium(id);
        return ResponseEntity.ok(ApiResponse.ok("Delete stadium successfully", null));
    }
}
