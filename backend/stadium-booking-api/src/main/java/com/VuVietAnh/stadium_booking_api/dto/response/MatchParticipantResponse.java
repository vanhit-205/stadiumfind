package com.VuVietAnh.stadium_booking_api.dto.response;

import com.VuVietAnh.stadium_booking_api.enums.MatchStatus;
import com.VuVietAnh.stadium_booking_api.enums.ParticipantStatus;
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
public class MatchParticipantResponse {

    private Long id;
    private Long matchPostId;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String userPhoneNumber;
    private ParticipantStatus status;
    private LocalDateTime joinedAt;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ====== Thông tin tóm tắt Kèo (MatchPost Summary) ======
    private String matchTitle;
    private String sportType;
    private LocalDate playDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String stadiumName;
    private String courtName;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private BigDecimal pricePerPerson;
    private MatchStatus matchStatus;
    private String creatorName;
    private String creatorAvatar;
    private String creatorPhone;
}

