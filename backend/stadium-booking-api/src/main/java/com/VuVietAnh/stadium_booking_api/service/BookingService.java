package com.VuVietAnh.stadium_booking_api.service;

import com.VuVietAnh.stadium_booking_api.dto.request.BookingCreateRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.BookingResponse;
import com.VuVietAnh.stadium_booking_api.enums.BookingStatus;
import com.VuVietAnh.stadium_booking_api.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingCreateRequest request);
    BookingResponse getBookingById(Long id);
    Page<BookingResponse> getBookingsByUser(Long userId, Pageable pageable);
    Page<BookingResponse> getBookingsByCourt(Long courtId, Pageable pageable);
    List<BookingResponse> getBookingsByCourtAndDate(Long courtId, LocalDate date);
    Page<BookingResponse> getBookingsByStadium(Long stadiumId, Pageable pageable);
    BookingResponse updateBookingStatus(Long id, BookingStatus status);
    BookingResponse updatePaymentStatus(Long id, PaymentStatus paymentStatus);
    void cancelBooking(Long id, Long userId);
}
