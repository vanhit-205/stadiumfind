package com.VuVietAnh.stadium_booking_api.dto.response;

import com.VuVietAnh.stadium_booking_api.enums.MatchStatus;
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
public class MatchPostResponse {

    private Long id;
    private Long creatorId;
    private String creatorName;
    private String creatorAvatar;
    private Long courtId;
    private String courtName;
    private Long stadiumId;
    private String stadiumName;
    private Long bookingId;
    private String title;
    private String description;
    private String sportType;
    private LocalDate playDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private BigDecimal pricePerPerson;
    private MatchStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
