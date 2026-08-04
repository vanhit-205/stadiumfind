package com.VuVietAnh.stadium_booking_api.dto.request;

import com.VuVietAnh.stadium_booking_api.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationCreateRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private NotificationType type;

    private Long referenceId;
}
