package com.VuVietAnh.stadium_booking_api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchParticipantRequest {

    @NotNull(message = "Match post ID is required")
    private Long matchPostId;

    private Long userId;

    private String note;
}
