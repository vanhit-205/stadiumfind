package com.VuVietAnh.stadium_booking_api.dto.response;

import com.VuVietAnh.stadium_booking_api.enums.CourtStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourtResponse {

    private Long id;
    private Long stadiumId;
    private String stadiumName;
    private String name;
    private String sportType;
    private BigDecimal pricePerHour;
    private CourtStatus status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
