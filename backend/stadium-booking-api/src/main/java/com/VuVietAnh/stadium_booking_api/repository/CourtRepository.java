package com.VuVietAnh.stadium_booking_api.repository;

import com.VuVietAnh.stadium_booking_api.entity.Court;
import com.VuVietAnh.stadium_booking_api.enums.CourtStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {

    // Lấy tất cả sân con của một cụm sân (Stadium)
    List<Court> findByStadiumId(Long stadiumId);

    // Phân trang danh sách sân con thuộc cụm sân theo Trạng thái (AVAILABLE, MAINTENANCE, INACTIVE)
    Page<Court> findByStadiumIdAndStatus(Long stadiumId, CourtStatus status, Pageable pageable);

    // Lấy danh sách sân con theo Loại môn thể thao (Football, Badminton, Pickleball, Tennis...)
    List<Court> findBySportType(String sportType);

    // Kiểm tra xem một cụm sân có tồn tại sân con nào không
    boolean existsByStadiumId(Long stadiumId);
}
