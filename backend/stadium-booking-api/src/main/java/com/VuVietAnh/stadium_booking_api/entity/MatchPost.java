package com.VuVietAnh.stadium_booking_api.entity;

import com.VuVietAnh.stadium_booking_api.enums.MatchStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "match_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be empty")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Sport type is required")
    @Column(name = "sport_type", nullable = false, length = 50)
    private String sportType;

    @NotNull(message = "Play date is required")
    @Column(name = "play_date", nullable = false)
    private LocalDate playDate;

    @NotNull(message = "Start time is required")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Min(value = 1, message = "Max participants must be at least 1")
    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Builder.Default
    @Column(name = "current_participants", nullable = false)
    private Integer currentParticipants = 1;

    @DecimalMin(value = "0.0", message = "Price per person cannot be negative")
    @Column(name = "price_per_person", precision = 12, scale = 2)
    private BigDecimal pricePerPerson;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private MatchStatus status = MatchStatus.OPEN;

    // Quan hệ N-1: Bài tìm đối được tạo bởi 1 User (Creator)
    @NotNull(message = "Creator cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    // Quan hệ N-1: Bài tìm đối thuộc về 1 Sân con cụ thể
    @NotNull(message = "Court cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    // Quan hệ 1-1 (hoặc N-1): Bài tìm đối có thể liên kết với một đơn đặt sân trước đó
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    // Quan hệ 1-N: Một bài đăng tìm đối có nhiều lượt đăng ký tham gia (MatchParticipant)
    @OneToMany(mappedBy = "matchPost", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MatchParticipant> participants = new ArrayList<>();
}
