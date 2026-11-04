package com.example.stadiumfind.features.booking.domain.model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Domain model đại diện cho kết quả đặt sân thành công (Vé đặt sân).
 * Sẵn sàng cho việc tạo mã QR và hiển thị thông tin ở Ngày 23.
 */
data class BookingResult(
    val id: Long,
    val userId: Long?,
    val userName: String?,
    val courtId: Long,
    val courtName: String,
    val stadiumId: Long?,
    val stadiumName: String,
    val stadiumAddress: String? = null,
    val stadiumImageUrl: String? = null,
    val bookingDate: String,
    val startTime: String,
    val endTime: String,
    val totalPrice: Double,
    val status: String,
    val paymentStatus: String,
    val note: String?,
    val qrCodeData: String? = null,
    val createdAt: String?
) {
    /**
     * Chuỗi định dạng hiển thị giờ đá gọn gàng (VD: "17:30 - 19:00")
     */
    val displayTimeRange: String
        get() {
            val start = if (startTime.length >= 5) startTime.substring(0, 5) else startTime
            val end = if (endTime.length >= 5) endTime.substring(0, 5) else endTime
            return "$start - $end"
        }

    /**
     * Định dạng tổng tiền theo VNĐ (VD: "300.000đ")
     */
    val formattedTotalPrice: String
        get() {
            if (totalPrice <= 0.0) return "0đ"
            val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
                groupingSeparator = '.'
            }
            val formatter = DecimalFormat("#,###", symbols)
            return "${formatter.format(totalPrice)}đ"
        }
}
