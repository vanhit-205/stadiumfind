package com.VuVietAnh.stadium_booking_api.repository;

import com.VuVietAnh.stadium_booking_api.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Phân trang danh sách tất cả thông báo của một Người dùng (Mới nhất sắp xếp theo Paging)
    Page<Notification> findByUserId(Long userId, Pageable pageable);

    // Phân trang danh sách thông báo CHƯA ĐỌC của Người dùng
    Page<Notification> findByUserIdAndIsReadFalse(Long userId, Pageable pageable);

    // Đếm số lượng thông báo chưa đọc của Người dùng (Dùng để hiển thị badge số lượng thông báo trên UI)
    long countByUserIdAndIsReadFalse(Long userId);
}
