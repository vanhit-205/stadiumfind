package com.VuVietAnh.stadium_booking_api.dto.response;

import com.VuVietAnh.stadium_booking_api.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;
    private Long userId;
    private String title;
    private String content;
    private NotificationType type;
    private Boolean isRead;
    private Long referenceId;
    private LocalDateTime createdAt;
}
