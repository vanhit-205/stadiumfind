package com.VuVietAnh.stadium_booking_api.entity;

import com.VuVietAnh.stadium_booking_api.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email format is invalid")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Full name cannot be empty")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Invalid phone number format")
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @NotNull(message = "Role cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Quan hệ 1-N: Một chủ sân (User) quản lý nhiều sân vận động (Stadium)
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Stadium> stadiums = new ArrayList<>();

    // Quan hệ 1-N: Một User có thể thực hiện nhiều lượt đặt sân (Booking)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    // Quan hệ 1-N: Một User có thể đăng nhiều bài tìm đối/ghép kèo (MatchPost)
    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
    @Builder.Default
    private List<MatchPost> createdMatchPosts = new ArrayList<>();

    // Quan hệ 1-N: Một User có thể tham gia nhiều bài ghép kèo (thông qua MatchParticipant)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MatchParticipant> matchParticipants = new ArrayList<>();

    // Quan hệ 1-N: Một User lưu nhiều sân yêu thích (thông qua Favorite)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Favorite> favorites = new ArrayList<>();

    // Quan hệ 1-N: Một User nhận nhiều thông báo (Notification)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();
}
