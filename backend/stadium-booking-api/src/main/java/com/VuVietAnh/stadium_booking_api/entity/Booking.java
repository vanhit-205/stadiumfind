package com.VuVietAnh.stadium_booking_api.entity;

import com.VuVietAnh.stadium_booking_api.enums.BookingStatus;
import com.VuVietAnh.stadium_booking_api.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Booking date is required")
    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @NotNull(message = "Start time is required")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total price must be greater than 0")
    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Column(name = "note")
    private String note;

    // Quan hệ N-1: Nhiều đơn đặt sân thuộc về 1 Người đặt (User)
    @NotNull(message = "User cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Quan hệ N-1: Nhiều đơn đặt sân liên quan đến 1 Sân con (Court)
    @NotNull(message = "Court cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    // Quan hệ 1-1 / N-1: Một đơn đặt sân có thể liên kết với một bài đăng tìm đối (MatchPost)
    @OneToOne(mappedBy = "booking", fetch = FetchType.LAZY)
    private MatchPost matchPost;
}
