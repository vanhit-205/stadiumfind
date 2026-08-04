package com.VuVietAnh.stadium_booking_api.entity;

import com.VuVietAnh.stadium_booking_api.enums.CourtStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Court extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Court name cannot be empty")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Sport type cannot be empty")
    @Column(name = "sport_type", nullable = false, length = 50)
    private String sportType; // Ví dụ: Football, Badminton, Tennis, Basketball, Pickleball

    @NotNull(message = "Price per hour is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price per hour must be greater than 0")
    @Column(name = "price_per_hour", nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerHour;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CourtStatus status = CourtStatus.AVAILABLE;

    @Column(name = "description")
    private String description;

    // Quan hệ N-1: Nhiều sân con (Court) thuộc về 1 Cụm sân (Stadium)
    @NotNull(message = "Stadium cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id", nullable = false)
    private Stadium stadium;

    // Quan hệ 1-N: Một sân con có nhiều lịch đặt (Booking)
    @OneToMany(mappedBy = "court", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    // Quan hệ 1-N: Một sân con có nhiều bài đăng tìm đối (MatchPost)
    @OneToMany(mappedBy = "court", fetch = FetchType.LAZY)
    @Builder.Default
    private List<MatchPost> matchPosts = new ArrayList<>();
}
