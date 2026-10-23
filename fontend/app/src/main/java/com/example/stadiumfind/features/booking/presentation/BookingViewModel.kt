package com.example.stadiumfind.features.booking.presentation

import androidx.lifecycle.ViewModel
import com.example.stadiumfind.features.booking.domain.model.CalendarDay
import com.example.stadiumfind.features.booking.domain.model.CourtItem
import com.example.stadiumfind.features.booking.domain.model.SlotDuration
import com.example.stadiumfind.features.booking.domain.model.TimeSlot
import com.example.stadiumfind.features.booking.domain.model.TimeSlotStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar
import javax.inject.Inject

/**
 * ViewModel quản lý logic chọn Ngày, Khung giờ (TimeSlot), Sân con và Thời lượng ca (60p / 90p).
 */
@HiltViewModel
class BookingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    private var isInitialized = false

    /**
     * Khởi tạo dữ liệu màn hình đặt sân từ SafeArgs hoặc dữ liệu mặc định.
     */
    fun initBooking(stadiumId: Long, stadiumName: String?, pricePerHour: Double?) {
        if (isInitialized && _uiState.value.stadiumId == stadiumId && stadiumId > 0L) {
            return
        }
        isInitialized = true

        val validPrice = if (pricePerHour != null && pricePerHour > 0.0) pricePerHour else 200000.0
        val validName = if (!stadiumName.isNullOrBlank()) stadiumName else "Sân bóng đá Chuyên Việt"

        val days = generateDays(21)
        val selectedDay = days.firstOrNull { it.isToday } ?: days.first()
        val courts = generateCourts(stadiumId)
        val selectedCourt = courts.firstOrNull()

        val duration = SlotDuration.DURATION_90_MIN
        val slots = generateTimeSlots(selectedDay, duration, validPrice)

        _uiState.value = BookingUiState(
            stadiumId = stadiumId,
            stadiumName = validName,
            basePricePerHour = validPrice,
            selectedDuration = duration,
            courts = courts,
            selectedCourt = selectedCourt,
            days = days,
            selectedDate = selectedDay,
            timeSlots = slots,
            selectedTimeSlot = null,
            estimatedPrice = 0.0,
            isConfirmEnabled = false
        )
    }

    /**
     * Chọn Ngày đá trên lịch ngang.
     */
    fun selectDate(day: CalendarDay) {
        val updatedDays = _uiState.value.days.map {
            it.copy(isSelected = (it.dateMillis == day.dateMillis))
        }

        val updatedSlots = generateTimeSlots(
            day,
            _uiState.value.selectedDuration,
            _uiState.value.basePricePerHour
        )

        // Reset khung giờ đang chọn nếu đổi sang ngày khác để tránh nhầm lẫn
        _uiState.update { current ->
            current.copy(
                days = updatedDays,
                selectedDate = day.copy(isSelected = true),
                timeSlots = updatedSlots,
                selectedTimeSlot = null,
                estimatedPrice = 0.0,
                isConfirmEnabled = false
            )
        }
    }

    /**
     * Chuyển đổi giữa Ca 60 phút và Ca 90 phút.
     */
    fun selectDuration(duration: SlotDuration) {
        if (_uiState.value.selectedDuration == duration) return

        val currentDate = _uiState.value.selectedDate ?: return
        val newSlots = generateTimeSlots(
            currentDate,
            duration,
            _uiState.value.basePricePerHour
        )

        _uiState.update { current ->
            current.copy(
                selectedDuration = duration,
                timeSlots = newSlots,
                selectedTimeSlot = null,
                estimatedPrice = 0.0,
                isConfirmEnabled = false
            )
        }
    }

    /**
     * Chọn Khung giờ trên lưới TimeSlot.
     */
    fun selectTimeSlot(slot: TimeSlot) {
        if (!slot.status.isSelectable) return

        val updatedSlots = _uiState.value.timeSlots.map { currentSlot ->
            if (currentSlot.id == slot.id) {
                currentSlot.copy(status = TimeSlotStatus.SELECTED)
            } else if (currentSlot.status == TimeSlotStatus.SELECTED) {
                currentSlot.copy(status = TimeSlotStatus.AVAILABLE)
            } else {
                currentSlot
            }
        }

        val selected = updatedSlots.firstOrNull { it.id == slot.id }
        val price = selected?.basePrice ?: 0.0

        _uiState.update { current ->
            current.copy(
                timeSlots = updatedSlots,
                selectedTimeSlot = selected,
                estimatedPrice = price,
                isConfirmEnabled = true
            )
        }
    }

    /**
     * Chọn Sân con trong cụm sân.
     */
    fun selectCourt(court: CourtItem) {
        val updatedCourts = _uiState.value.courts.map {
            it.copy(isSelected = (it.id == court.id))
        }
        val selected = updatedCourts.firstOrNull { it.id == court.id }

        _uiState.update { current ->
            current.copy(
                courts = updatedCourts,
                selectedCourt = selected
            )
        }
    }

    /**
     * Cập nhật ghi chú đặt sân của người dùng.
     */
    fun updateNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    // ==========================================
    // HELPER FUNCTIONS
    // ==========================================

    /**
     * Sinh danh sách các ngày kế tiếp (mặc định 21 ngày).
     */
    private fun generateDays(count: Int): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()
        val calendar = Calendar.getInstance()

        val todayYear = calendar.get(Calendar.YEAR)
        val todayDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        for (i in 0 until count) {
            val dateMillis = calendar.timeInMillis
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)
            val dayOfWeekInt = calendar.get(Calendar.DAY_OF_WEEK)

            val isToday = (year == todayYear && calendar.get(Calendar.DAY_OF_YEAR) == todayDayOfYear)

            val dowString = when (dayOfWeekInt) {
                Calendar.MONDAY -> "Th 2"
                Calendar.TUESDAY -> "Th 3"
                Calendar.WEDNESDAY -> "Th 4"
                Calendar.THURSDAY -> "Th 5"
                Calendar.FRIDAY -> "Th 6"
                Calendar.SATURDAY -> "Th 7"
                Calendar.SUNDAY -> "CN"
                else -> ""
            }

            days.add(
                CalendarDay(
                    dateMillis = dateMillis,
                    dayOfWeek = dowString,
                    dayOfMonth = dayOfMonth,
                    month = month,
                    year = year,
                    isToday = isToday,
                    isSelected = isToday
                )
            )

            // Tiến sang ngày tiếp theo
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return days
    }

    /**
     * Sinh danh sách các khung giờ theo thời lượng (60p hoặc 90p) và kiểm tra ca quá khứ.
     */
    private fun generateTimeSlots(
        day: CalendarDay,
        duration: SlotDuration,
        pricePerHour: Double
    ): List<TimeSlot> {
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)

        val slotTemplates = if (duration == SlotDuration.DURATION_60_MIN) {
            listOf(
                SlotTemplate("06:00", "07:00", 6, 0),
                SlotTemplate("07:00", "08:00", 7, 0),
                SlotTemplate("08:00", "09:00", 8, 0),
                SlotTemplate("09:00", "10:00", 9, 0),
                SlotTemplate("15:00", "16:00", 15, 0),
                SlotTemplate("16:00", "17:00", 16, 0),
                SlotTemplate("17:00", "18:00", 17, 0),
                SlotTemplate("18:00", "19:00", 18, 0),
                SlotTemplate("19:00", "20:00", 19, 0),
                SlotTemplate("20:00", "21:00", 20, 0),
                SlotTemplate("21:00", "22:00", 21, 0),
                SlotTemplate("22:00", "23:00", 22, 0)
            )
        } else {
            listOf(
                SlotTemplate("06:00", "07:30", 6, 0),
                SlotTemplate("07:30", "09:00", 7, 30),
                SlotTemplate("09:00", "10:30", 9, 0),
                SlotTemplate("14:30", "16:00", 14, 30),
                SlotTemplate("16:00", "17:30", 16, 0),
                SlotTemplate("17:30", "19:00", 17, 30),
                SlotTemplate("19:00", "20:30", 19, 0),
                SlotTemplate("20:30", "22:00", 20, 30),
                SlotTemplate("22:00", "23:30", 22, 0)
            )
        }

        val calculatedPrice = pricePerHour * duration.durationHours

        return slotTemplates.mapIndexed { index, t ->
            val isPast = if (day.isToday) {
                (t.startHour < currentHour) || (t.startHour == currentHour && t.startMinute <= currentMinute)
            } else {
                false
            }

            TimeSlot(
                id = "slot_${duration.shortLabel}_${index}_${t.startHour}_${t.startMinute}",
                startTime = t.startTime,
                endTime = t.endTime,
                duration = duration,
                startHour = t.startHour,
                startMinute = t.startMinute,
                status = if (isPast) TimeSlotStatus.PAST else TimeSlotStatus.AVAILABLE,
                basePrice = calculatedPrice
            )
        }
    }

    /**
     * Sinh danh sách sân con giả lập thuộc sân vận động.
     */
    private fun generateCourts(stadiumId: Long): List<CourtItem> {
        return listOf(
            CourtItem(id = 101L, name = "Sân 5 - Số 1", sportType = "Bóng đá 5 người", isSelected = true),
            CourtItem(id = 102L, name = "Sân 5 - Số 2", sportType = "Bóng đá 5 người", isSelected = false),
            CourtItem(id = 103L, name = "Sân 7 - VIP", sportType = "Bóng đá 7 người", isSelected = false),
            CourtItem(id = 104L, name = "Sân 7 - Tiêu chuẩn", sportType = "Bóng đá 7 người", isSelected = false)
        )
    }

    private data class SlotTemplate(
        val startTime: String,
        val endTime: String,
        val startHour: Int,
        val startMinute: Int
    )
}
