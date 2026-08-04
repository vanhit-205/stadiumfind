package com.VuVietAnh.stadium_booking_api.repository;

import com.VuVietAnh.stadium_booking_api.entity.Booking;
import com.VuVietAnh.stadium_booking_api.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Phân trang danh sách đơn đặt sân của một Người dùng (User)
    Page<Booking> findByUserId(Long userId, Pageable pageable);

    // Phân trang đơn đặt sân của một sân con (Court)
    Page<Booking> findByCourtId(Long courtId, Pageable pageable);

    // Lấy danh sách các đơn đặt sân của một sân con vào một ngày cụ thể (Phục vụ việc hiển thị khung giờ đã trùng)
    List<Booking> findByCourtIdAndBookingDate(Long courtId, LocalDate bookingDate);

    // Phân trang đơn đặt sân theo Trạng thái đơn (PENDING, CONFIRMED, CANCELLED, COMPLETED)
    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);

    // Lấy các đơn đặt sân trong một cụm sân cụ thể (Dành cho chủ sân xem tất cả lịch đặt)
    Page<Booking> findByCourtStadiumId(Long stadiumId, Pageable pageable);

    // Kiểm tra xem khung giờ dự định đặt sân có bị trùng với đơn đặt hiện có (khác trạng thái CANCELLED) không
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
           "WHERE b.court.id = :courtId " +
           "AND b.bookingDate = :bookingDate " +
           "AND b.status <> 'CANCELLED' " +
           "AND ((b.startTime < :endTime AND b.endTime > :startTime))")
    boolean existsOverlappingBooking(@Param("courtId") Long courtId,
                                     @Param("bookingDate") LocalDate bookingDate,
                                     @Param("startTime") LocalTime startTime,
                                     @Param("endTime") LocalTime endTime);
}
