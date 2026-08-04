package com.VuVietAnh.stadium_booking_api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchPostCreateRequest {

    @NotNull(message = "Creator ID is required")
    private Long creatorId;

    @NotNull(message = "Court ID is required")
    private Long courtId;

    private Long bookingId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Sport type is required")
    private String sportType;

    @NotNull(message = "Play date is required")
    @FutureOrPresent(message = "Play date must be today or in the future")
    private LocalDate playDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @Min(value = 1, message = "Max participants must be at least 1")
    private Integer maxParticipants;

    @DecimalMin(value = "0.0", message = "Price per person cannot be negative")
    private BigDecimal pricePerPerson;
}
