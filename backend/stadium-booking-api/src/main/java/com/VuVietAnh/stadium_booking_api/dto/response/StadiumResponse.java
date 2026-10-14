package com.VuVietAnh.stadium_booking_api.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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
    private Double latitude;
    private Double longitude;
    private String phoneNumber;
    private String description;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private String imageUrl;
    private Double rating;
    private BigDecimal minPricePerHour;
    private Integer totalCourts;
    private List<String> sportTypes;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
