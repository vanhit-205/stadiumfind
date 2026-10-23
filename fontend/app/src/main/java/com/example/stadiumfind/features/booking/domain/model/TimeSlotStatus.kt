package com.example.stadiumfind.features.booking.domain.model

/**
 * Trạng thái của một khung giờ (TimeSlot):
 * - AVAILABLE: Khung giờ còn trống, người dùng có thể click chọn
 * - SELECTED: Người dùng đang chọn khung giờ này
 * - BOOKED: Đã có người đặt trước (sẽ kết nối API ở Ngày 20)
 * - PAST: Khung giờ đã qua trong quá khứ so với thời gian hiện tại
 */
enum class TimeSlotStatus {
    AVAILABLE,
    SELECTED,
    BOOKED,
    PAST;

    val isSelectable: Boolean
        get() = this == AVAILABLE || this == SELECTED
}
