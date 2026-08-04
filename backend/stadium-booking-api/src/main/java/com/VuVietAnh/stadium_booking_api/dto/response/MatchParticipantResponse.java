package com.VuVietAnh.stadium_booking_api.dto.response;

import com.VuVietAnh.stadium_booking_api.enums.ParticipantStatus;
import lombok.*;

import java.time.LocalDateTime;

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
}
