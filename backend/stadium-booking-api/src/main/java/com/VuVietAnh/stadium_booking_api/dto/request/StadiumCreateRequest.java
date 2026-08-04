package com.VuVietAnh.stadium_booking_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StadiumCreateRequest {

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    @NotBlank(message = "Stadium name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Province/City is required")
    private String provinceCity;

    @NotBlank(message = "District is required")
    private String district;

    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Invalid phone number format")
    private String phoneNumber;

    private String description;

    private LocalTime openingTime;

    private LocalTime closingTime;

    private String imageUrl;
}
