package com.VuVietAnh.stadium_booking_api.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StadiumResponse {

    private Long id;
    private String name;
    private String address;
    private String provinceCity;
    private String district;
    private String phoneNumber;
    private String description;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private String imageUrl;
    private Double rating;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
