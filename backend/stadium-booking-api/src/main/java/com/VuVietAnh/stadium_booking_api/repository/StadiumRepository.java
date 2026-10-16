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

    // Tìm các sân có tọa độ nằm trong Bounding Box (cho tìm kiếm lân cận)
    @Query("SELECT s FROM Stadium s WHERE " +
           "s.latitude IS NOT NULL AND s.longitude IS NOT NULL AND " +
           "s.latitude BETWEEN :minLat AND :maxLat AND " +
           "s.longitude BETWEEN :minLng AND :maxLng")
    java.util.List<Stadium> findStadiumsInBoundingBox(@Param("minLat") Double minLat,
                                                     @Param("maxLat") Double maxLat,
                                                     @Param("minLng") Double minLng,
                                                     @Param("maxLng") Double maxLng);

    // Lấy tất cả các sân có tọa độ hợp lệ
    @Query("SELECT s FROM Stadium s WHERE s.latitude IS NOT NULL AND s.longitude IS NOT NULL")
    java.util.List<Stadium> findAllWithCoordinates();
}
