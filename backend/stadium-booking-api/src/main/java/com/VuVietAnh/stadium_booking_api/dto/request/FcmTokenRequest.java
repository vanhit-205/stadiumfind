package com.VuVietAnh.stadium_booking_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmTokenRequest {
    @NotBlank(message = "FCM token cannot be empty")
    private String fcmToken;
}
