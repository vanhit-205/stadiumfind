package com.VuVietAnh.stadium_booking_api.repository;

import com.VuVietAnh.stadium_booking_api.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // Phân trang danh sách sân bóng yêu thích của một Người dùng
    Page<Favorite> findByUserId(Long userId, Pageable pageable);

    // Tìm bản ghi yêu thích cụ thể giữa User và Stadium
    Optional<Favorite> findByUserIdAndStadiumId(Long userId, Long stadiumId);

    // Kiểm tra xem người dùng đã thả tim sân này chưa
    boolean existsByUserIdAndStadiumId(Long userId, Long stadiumId);

    // Xóa bản ghi yêu thích của người dùng đối với một sân bóng cụ thể (Hành động bỏ yêu thích)
    @Transactional
    @Modifying
    void deleteByUserIdAndStadiumId(Long userId, Long stadiumId);
}
