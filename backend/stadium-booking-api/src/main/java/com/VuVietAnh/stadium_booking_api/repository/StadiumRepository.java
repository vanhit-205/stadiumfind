package com.VuVietAnh.stadium_booking_api.repository;

import com.VuVietAnh.stadium_booking_api.entity.Stadium;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StadiumRepository extends JpaRepository<Stadium, Long> {

    // Phân trang danh sách sân bóng thuộc về một Chủ sân (Owner) cụ thể
    Page<Stadium> findByOwnerId(Long ownerId, Pageable pageable);

    // Phân trang danh sách sân bóng theo Tỉnh/Thành phố và Quận/Huyện
    Page<Stadium> findByProvinceCityAndDistrict(String provinceCity, String district, Pageable pageable);

    // Phân trang danh sách sân bóng theo Tỉnh/Thành phố
    Page<Stadium> findByProvinceCity(String provinceCity, Pageable pageable);

    // Tìm kiếm sân bóng theo tên (không phân biệt hoa/thường)
    Page<Stadium> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Tìm kiếm sân bóng tổng hợp linh hoạt (theo từ khóa tên sân và Tỉnh/Thành phố)
    @Query("SELECT s FROM Stadium s WHERE " +
           "(:provinceCity IS NULL OR s.provinceCity = :provinceCity) AND " +
           "(:keyword IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.address) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Stadium> searchStadiums(@Param("provinceCity") String provinceCity,
                                 @Param("keyword") String keyword,
                                 Pageable pageable);
}
