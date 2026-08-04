package com.VuVietAnh.stadium_booking_api.dto.response;

import com.VuVietAnh.stadium_booking_api.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private Role role;
    private String avatarUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
