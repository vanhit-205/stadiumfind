package com.VuVietAnh.stadium_booking_api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Stadium ID is required")
    private Long stadiumId;
}
