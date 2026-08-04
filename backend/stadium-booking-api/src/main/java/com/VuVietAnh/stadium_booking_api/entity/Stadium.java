package com.VuVietAnh.stadium_booking_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stadiums")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stadium extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Stadium name cannot be empty")
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @NotBlank(message = "Address cannot be empty")
    @Column(name = "address", nullable = false)
    private String address;

    @NotBlank(message = "Province/City cannot be empty")
    @Column(name = "province_city", nullable = false, length = 100)
    private String provinceCity;

    @NotBlank(message = "District cannot be empty")
    @Column(name = "district", nullable = false, length = 100)
    private String district;

    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Invalid phone number format")
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "rating")
    private Double rating;

    // Quan hệ N-1: Nhiều sân vận động thuộc về 1 Chủ sân (User)
    @NotNull(message = "Stadium owner cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Quan hệ 1-N: Một Stadium gồm nhiều sân con (Court). Xóa Stadium thì xóa hết Court của nó.
    @OneToMany(mappedBy = "stadium", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Court> courts = new ArrayList<>();

    // Quan hệ 1-N: Sân vận động xuất hiện trong danh sách yêu thích của nhiều người dùng
    @OneToMany(mappedBy = "stadium", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Favorite> favorites = new ArrayList<>();
}
