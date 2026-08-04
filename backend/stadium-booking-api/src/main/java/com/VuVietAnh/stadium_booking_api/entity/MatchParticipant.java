package com.VuVietAnh.stadium_booking_api.entity;

import com.VuVietAnh.stadium_booking_api.enums.ParticipantStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "match_participants",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_match_user", columnNames = {"match_post_id", "user_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ N-1: Thuộc về bài đăng ghép kèo nào
    @NotNull(message = "Match post is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_post_id", nullable = false)
    private MatchPost matchPost;

    // Quan hệ N-1: Người dùng đăng ký tham gia
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ParticipantStatus status = ParticipantStatus.PENDING;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "note")
    private String note;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        if (this.joinedAt == null) {
            this.joinedAt = LocalDateTime.now();
        }
    }
}
