package com.VuVietAnh.stadium_booking_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(
    name = "favorites",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_stadium", columnNames = {"user_id", "stadium_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ N-1: Người dùng đánh dấu yêu thích
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Quan hệ N-1: Sân vận động được yêu thích
    @NotNull(message = "Stadium is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id", nullable = false)
    private Stadium stadium;
}
