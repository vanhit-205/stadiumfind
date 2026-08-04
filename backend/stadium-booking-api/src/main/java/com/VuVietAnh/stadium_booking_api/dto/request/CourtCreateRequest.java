package com.VuVietAnh.stadium_booking_api.dto.request;

import com.VuVietAnh.stadium_booking_api.enums.CourtStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourtCreateRequest {

    @NotNull(message = "Stadium ID is required")
    private Long stadiumId;

    @NotBlank(message = "Court name is required")
    private String name;

    @NotBlank(message = "Sport type is required")
    private String sportType;

    @NotNull(message = "Price per hour is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price per hour must be greater than 0")
    private BigDecimal pricePerHour;

    private CourtStatus status;

    private String description;
}
