package com.VuVietAnh.stadium_booking_api.dto.response;

import com.VuVietAnh.stadium_booking_api.enums.BookingStatus;
import com.VuVietAnh.stadium_booking_api.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private Long id;
    private Long userId;
    private String userName;
    private Long courtId;
    private String courtName;
    private Long stadiumId;
    private String stadiumName;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal totalPrice;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
