package com.VuVietAnh.stadium_booking_api.repository;

import com.VuVietAnh.stadium_booking_api.entity.MatchParticipant;
import com.VuVietAnh.stadium_booking_api.enums.ParticipantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, Long> {

    // Lấy danh sách thành viên tham gia của một bài ghép kèo cụ thể
    List<MatchParticipant> findByMatchPostId(Long matchPostId);

    // Lấy danh sách các thành viên theo Trạng thái (PENDING, ACCEPTED, REJECTED) trong một kèo
    List<MatchParticipant> findByMatchPostIdAndStatus(Long matchPostId, ParticipantStatus status);

    // Phân trang danh sách các bài ghép kèo mà một Người dùng đã đăng ký tham gia
    Page<MatchParticipant> findByUserId(Long userId, Pageable pageable);

    // Tìm kiếm bản ghi tham gia của một người dùng vào một bài ghép kèo cụ thể
    Optional<MatchParticipant> findByMatchPostIdAndUserId(Long matchPostId, Long userId);

    // Kiểm tra xem người dùng đã gửi yêu cầu tham gia bài ghép kèo này chưa
    boolean existsByMatchPostIdAndUserId(Long matchPostId, Long userId);

    // Đếm số lượng thành viên đã được duyệt (ACCEPTED) trong một trận đấu
    long countByMatchPostIdAndStatus(Long matchPostId, ParticipantStatus status);
}
