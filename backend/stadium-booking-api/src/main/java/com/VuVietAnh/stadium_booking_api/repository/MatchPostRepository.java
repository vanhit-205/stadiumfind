package com.VuVietAnh.stadium_booking_api.repository;

import com.VuVietAnh.stadium_booking_api.entity.MatchPost;
import com.VuVietAnh.stadium_booking_api.enums.MatchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface MatchPostRepository extends JpaRepository<MatchPost, Long> {

    // Phân trang bài ghép kèo theo Trạng thái (OPEN, FULL, CANCELLED, COMPLETED)
    Page<MatchPost> findByStatus(MatchStatus status, Pageable pageable);

    // Phân trang danh sách bài ghép kèo do một Người dùng tạo ra
    Page<MatchPost> findByCreatorId(Long creatorId, Pageable pageable);

    // Phân trang danh sách bài ghép kèo theo Môn thể thao và Trạng thái
    Page<MatchPost> findBySportTypeAndStatus(String sportType, MatchStatus status, Pageable pageable);

    // Lấy các bài ghép kèo có ngày chơi từ hôm nay trở đi (Phục vụ việc xem kèo sắp diễn ra)
    Page<MatchPost> findByPlayDateGreaterThanEqualAndStatus(LocalDate playDate, MatchStatus status, Pageable pageable);

    // Tìm kiếm bài ghép kèo linh hoạt theo môn thể thao, từ ngày và trạng thái
    @Query("SELECT m FROM MatchPost m WHERE " +
           "(:sportType IS NULL OR m.sportType = :sportType) AND " +
           "(:status IS NULL OR m.status = :status) AND " +
           "(:fromDate IS NULL OR m.playDate >= :fromDate)")
    Page<MatchPost> searchMatchPosts(@Param("sportType") String sportType,
                                     @Param("status") MatchStatus status,
                                     @Param("fromDate") LocalDate fromDate,
                                     Pageable pageable);
}
