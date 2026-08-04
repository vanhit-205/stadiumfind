package com.VuVietAnh.stadium_booking_api.service.impl;

import com.VuVietAnh.stadium_booking_api.dto.request.BookingCreateRequest;
import com.VuVietAnh.stadium_booking_api.dto.response.BookingResponse;
import com.VuVietAnh.stadium_booking_api.entity.Booking;
import com.VuVietAnh.stadium_booking_api.entity.Court;
import com.VuVietAnh.stadium_booking_api.entity.User;
import com.VuVietAnh.stadium_booking_api.enums.BookingStatus;
import com.VuVietAnh.stadium_booking_api.enums.CourtStatus;
import com.VuVietAnh.stadium_booking_api.enums.PaymentStatus;
import com.VuVietAnh.stadium_booking_api.exception.BadRequestException;
import com.VuVietAnh.stadium_booking_api.exception.BookingConflictException;
import com.VuVietAnh.stadium_booking_api.exception.ResourceNotFoundException;
import com.VuVietAnh.stadium_booking_api.repository.BookingRepository;
import com.VuVietAnh.stadium_booking_api.repository.CourtRepository;
import com.VuVietAnh.stadium_booking_api.repository.UserRepository;
import com.VuVietAnh.stadium_booking_api.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CourtRepository courtRepository;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingCreateRequest request) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BadRequestException("End time must be after start time");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new ResourceNotFoundException("Court not found with id: " + request.getCourtId()));

        if (court.getStatus() != CourtStatus.AVAILABLE) {
            throw new BadRequestException("Court is currently unavailable for booking");
        }

        // Kiểm tra trùng lịch đặt
        boolean isOverlapping = bookingRepository.existsOverlappingBooking(
                request.getCourtId(),
                request.getBookingDate(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (isOverlapping) {
            throw new BookingConflictException("Court is already booked for the selected time slot");
        }

        // Tính tổng tiền = Số giờ * giá mỗi giờ
        long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
        double hours = minutes / 60.0;
        BigDecimal totalPrice = court.getPricePerHour().multiply(BigDecimal.valueOf(hours));

        Booking booking = Booking.builder()
                .user(user)
                .court(court)
                .bookingDate(request.getBookingDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .totalPrice(totalPrice)
                .status(BookingStatus.PENDING)
                .paymentStatus(PaymentStatus.UNPAID)
                .note(request.getNote())
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        return mapToResponse(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        return mapToResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponse> getBookingsByUser(Long userId, Pageable pageable) {
        return bookingRepository.findByUserId(userId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponse> getBookingsByCourt(Long courtId, Pageable pageable) {
        return bookingRepository.findByCourtId(courtId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByCourtAndDate(Long courtId, LocalDate date) {
        return bookingRepository.findByCourtIdAndBookingDate(courtId, date).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponse> getBookingsByStadium(Long stadiumId, Pageable pageable) {
        return bookingRepository.findByCourtStadiumId(stadiumId, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional
    public BookingResponse updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        booking.setStatus(status);
        Booking updated = bookingRepository.save(booking);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public BookingResponse updatePaymentStatus(Long id, PaymentStatus paymentStatus) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        booking.setPaymentStatus(paymentStatus);
        Booking updated = bookingRepository.save(booking);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void cancelBooking(Long id, Long userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (!booking.getUser().getId().equals(userId)) {
            throw new BadRequestException("You do not have permission to cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a completed booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getFullName())
                .courtId(booking.getCourt().getId())
                .courtName(booking.getCourt().getName())
                .stadiumId(booking.getCourt().getStadium().getId())
                .stadiumName(booking.getCourt().getStadium().getName())
                .bookingDate(booking.getBookingDate())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .paymentStatus(booking.getPaymentStatus())
                .note(booking.getNote())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}
