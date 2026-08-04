package com.VuVietAnh.stadium_booking_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    @NotBlank(message = "Full name cannot be blank")
    private String fullName;

    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Invalid phone number format")
    private String phoneNumber;

    private String avatarUrl;
}
