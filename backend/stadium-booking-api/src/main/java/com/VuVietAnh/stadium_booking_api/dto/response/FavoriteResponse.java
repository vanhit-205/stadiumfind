package com.VuVietAnh.stadium_booking_api.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteResponse {

    private Long id;
    private Long userId;
    private StadiumResponse stadium;
    private LocalDateTime createdAt;
}
