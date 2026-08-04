package com.VuVietAnh.stadium_booking_api.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCreateRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Court ID is required")
    private Long courtId;

    @NotNull(message = "Booking date is required")
    @FutureOrPresent(message = "Booking date must be today or in the future")
    private LocalDate bookingDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    private String note;
}
