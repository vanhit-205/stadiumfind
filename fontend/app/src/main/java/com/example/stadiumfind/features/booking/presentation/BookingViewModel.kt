package com.example.stadiumfind.features.booking.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.booking.domain.model.CalendarDay
import com.example.stadiumfind.features.booking.domain.model.CourtItem
import com.example.stadiumfind.features.booking.domain.model.OccupiedSlot
import com.example.stadiumfind.features.booking.domain.model.SlotDuration
import com.example.stadiumfind.features.booking.domain.model.TimeSlot
import com.example.stadiumfind.features.booking.domain.model.TimeSlotStatus
import com.example.stadiumfind.features.booking.domain.usecase.GetCourtsUseCase
import com.example.stadiumfind.features.booking.domain.usecase.GetOccupiedSlotsUseCase
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import com.example.stadiumfind.features.stadium.domain.usecase.GetStadiumsListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * ViewModel quản lý logic chọn Cụm sân, Sân con, Ngày đá, Khung giờ và Đặt nhiều ca đá cùng lúc.
 */
@HiltViewModel
class BookingViewModel @Inject constructor(
    private val getStadiumsListUseCase: GetStadiumsListUseCase,
    private val getCourtsUseCase: GetCourtsUseCase,
    private val getOccupiedSlotsUseCase: GetOccupiedSlotsUseCase
) : ViewModel() {

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
        val defaultCourts = generateCourts(stadiumId)
        val defaultCourt = defaultCourts.firstOrNull()

        val duration = SlotDuration.DURATION_90_MIN
        val initialSlots = generateTimeSlots(selectedDay, duration, validPrice, emptyList())

        _uiState.value = BookingUiState(
            stadiumId = stadiumId,
            stadiumName = validName,
            basePricePerHour = validPrice,
            selectedDuration = duration,
            courts = defaultCourts,
            selectedCourt = defaultCourt,
            days = days,
            selectedDate = selectedDay,
            timeSlots = initialSlots,
            selectedTimeSlots = emptyList(),
            estimatedPrice = 0.0,
            isConfirmEnabled = false
        )

        // 1. Tải danh sách tất cả Cụm sân (để phục vụ đổi sân/chọn sân)
        loadStadiums(stadiumId)

        // 2. Tải danh sách sân con thật từ API backend
        loadCourts(stadiumId)
    }

    /**
     * Tải danh sách các Cụm sân bóng để người dùng chọn hoặc đổi sân.
     */
    fun loadStadiums(currentStadiumId: Long = _uiState.value.stadiumId) {
        viewModelScope.launch {
            _uiState.update { it.copy(isStadiumsLoading = true) }
            when (val result = getStadiumsListUseCase()) {
                is Resource.Success -> {
                    val stadiumList = result.data
                    val matchedStadium = stadiumList.firstOrNull { it.id == currentStadiumId }
                        ?: stadiumList.firstOrNull()

                    _uiState.update { current ->
                        current.copy(
                            stadiums = stadiumList,
                            selectedStadium = matchedStadium,
                            stadiumName = matchedStadium?.name ?: current.stadiumName,
                            stadiumAddress = matchedStadium?.address ?: current.stadiumAddress,
                            basePricePerHour = if ((matchedStadium?.minPricePerHour ?: 0.0) > 0.0) {
                                matchedStadium!!.minPricePerHour
                            } else {
                                current.basePricePerHour
                            },
                            isStadiumsLoading = false
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isStadiumsLoading = false) }
                }
                is Resource.Loading -> {
                    _uiState.update { it.copy(isStadiumsLoading = true) }
                }
            }
        }
    }

    /**
     * Người dùng chọn một Cụm sân bóng mới từ BottomSheet/Picker.
     */
    fun selectStadium(stadium: StadiumItem) {
        if (_uiState.value.stadiumId == stadium.id) return

        val validPrice = if (stadium.minPricePerHour > 0.0) stadium.minPricePerHour else _uiState.value.basePricePerHour

        _uiState.update { current ->
            current.copy(
                stadiumId = stadium.id,
                stadiumName = stadium.name,
                stadiumAddress = stadium.address,
                basePricePerHour = validPrice,
                selectedStadium = stadium,
                selectedTimeSlots = emptyList(),
                estimatedPrice = 0.0,
                isConfirmEnabled = false
            )
        }

        // Tự động tải danh sách sân con của Cụm sân mới
        loadCourts(stadium.id)
    }

    /**
     * Tải danh sách sân con từ server theo stadiumId.
     */
    fun loadCourts(stadiumId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCourtsLoading = true) }
            when (val result = getCourtsUseCase(stadiumId)) {
                is Resource.Success -> {
                    val courts = result.data
                    if (courts.isNotEmpty()) {
                        val firstCourt = courts.first().copy(isSelected = true)
                        val updatedCourts = courts.mapIndexed { idx, c -> c.copy(isSelected = (idx == 0)) }
                        _uiState.update {
                            it.copy(
                                courts = updatedCourts,
                                selectedCourt = firstCourt,
                                selectedTimeSlots = emptyList(),
                                estimatedPrice = 0.0,
                                isConfirmEnabled = false,
                                isCourtsLoading = false
                            )
                        }
                        // Tải các khung giờ đã đặt cho sân con đầu tiên
                        _uiState.value.selectedDate?.let { date ->
                            loadOccupiedSlots(firstCourt.id, date.apiDateString)
                        }
                    } else {
                        _uiState.update { it.copy(isCourtsLoading = false) }
                        _uiState.value.selectedCourt?.let { court ->
                            _uiState.value.selectedDate?.let { date ->
                                loadOccupiedSlots(court.id, date.apiDateString)
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    // Nếu lỗi kết nối, giữ nguyên fallback courts để người dùng không bị gián đoạn trải nghiệm
                    _uiState.update { it.copy(isCourtsLoading = false) }
                    _uiState.value.selectedCourt?.let { court ->
                        _uiState.value.selectedDate?.let { date ->
                            loadOccupiedSlots(court.id, date.apiDateString)
                        }
                    }
                }
                is Resource.Loading -> {
                    _uiState.update { it.copy(isCourtsLoading = true) }
                }
            }
        }
    }

    /**
     * Tải danh sách khung giờ đã được đặt từ server cho một sân con trong ngày.
     */
    fun loadOccupiedSlots(courtId: Long, apiDateString: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSlotsLoading = true) }

            val result = getOccupiedSlotsUseCase(courtId, apiDateString)
            val occupiedList = if (result is Resource.Success) result.data else emptyList()

            val currentDate = _uiState.value.selectedDate ?: return@launch
            val duration = _uiState.value.selectedDuration
            val price = _uiState.value.basePricePerHour

            val refreshedSlots = generateTimeSlots(currentDate, duration, price, occupiedList)

            // Kiểm tra và giữ lại các ca đã chọn nếu vẫn còn AVAILABLE
            val currentSelectedIds = _uiState.value.selectedTimeSlots.map { it.id }.toSet()
            val validSelectedSlots = refreshedSlots.filter { slot ->
                currentSelectedIds.contains(slot.id) && slot.status.isSelectable
            }.map { it.copy(status = TimeSlotStatus.SELECTED) }

            // Cập nhật lại trạng thái SELECTED trên danh sách slots hiển thị
            val validSelectedIds = validSelectedSlots.map { it.id }.toSet()
            val finalSlots = refreshedSlots.map { slot ->
                if (validSelectedIds.contains(slot.id)) {
                    slot.copy(status = TimeSlotStatus.SELECTED)
                } else {
                    slot
                }
            }

            val totalPrice = validSelectedSlots.sumOf { it.basePrice }

            _uiState.update { state ->
                state.copy(
                    occupiedSlots = occupiedList,
                    timeSlots = finalSlots,
                    selectedTimeSlots = validSelectedSlots,
                    estimatedPrice = totalPrice,
                    isConfirmEnabled = validSelectedSlots.isNotEmpty(),
                    isSlotsLoading = false,
                    errorMessage = if (result is Resource.Error) result.message else null
                )
            }
        }
    }

    /**
     * Chọn Ngày đá trên lịch ngang -> Tải lại danh sách giờ đã đặt.
     */
    fun selectDate(day: CalendarDay) {
        val updatedDays = _uiState.value.days.map {
            it.copy(isSelected = (it.dateMillis == day.dateMillis))
        }

        val updatedSlots = generateTimeSlots(
            day,
            _uiState.value.selectedDuration,
            _uiState.value.basePricePerHour,
            emptyList()
        )

        _uiState.update { current ->
            current.copy(
                days = updatedDays,
                selectedDate = day.copy(isSelected = true),
                timeSlots = updatedSlots,
                selectedTimeSlots = emptyList(),
                estimatedPrice = 0.0,
                isConfirmEnabled = false
            )
        }

        val courtId = _uiState.value.selectedCourt?.id ?: 1L
        loadOccupiedSlots(courtId, day.apiDateString)
    }

    /**
     * Chọn Sân con trong cụm sân -> Tải lại danh sách giờ đã đặt của sân con đó.
     */
    fun selectCourt(court: CourtItem) {
        val updatedCourts = _uiState.value.courts.map {
            it.copy(isSelected = (it.id == court.id))
        }
        val selected = updatedCourts.firstOrNull { it.id == court.id }

        _uiState.update { current ->
            current.copy(
                courts = updatedCourts,
                selectedCourt = selected,
                selectedTimeSlots = emptyList(),
                estimatedPrice = 0.0,
                isConfirmEnabled = false
            )
        }

        val dateString = _uiState.value.selectedDate?.apiDateString.orEmpty()
        if (dateString.isNotEmpty()) {
            loadOccupiedSlots(court.id, dateString)
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
            _uiState.value.basePricePerHour,
            _uiState.value.occupiedSlots
        )

        _uiState.update { current ->
            current.copy(
                selectedDuration = duration,
                timeSlots = newSlots,
                selectedTimeSlots = emptyList(),
                estimatedPrice = 0.0,
                isConfirmEnabled = false
            )
        }
    }

    /**
     * Chọn / Hủy chọn Khung giờ trên lưới TimeSlot theo cơ chế Toggle (Multi-Select).
     * Cho phép khách hàng đặt 1 hoặc NHIỀU ca đá cùng lúc.
     */
    fun selectTimeSlot(slot: TimeSlot) {
        if (!slot.status.isSelectable) return

        val currentSelectedList = _uiState.value.selectedTimeSlots.toMutableList()
        val isAlreadySelected = currentSelectedList.any { it.id == slot.id }

        val updatedSlots = _uiState.value.timeSlots.map { currentSlot ->
            if (currentSlot.id == slot.id) {
                if (isAlreadySelected) {
                    // Đang chọn -> Click để Hủy chọn
                    currentSlot.copy(status = TimeSlotStatus.AVAILABLE)
                } else {
                    // Chưa chọn -> Click để Chọn
                    currentSlot.copy(status = TimeSlotStatus.SELECTED)
                }
            } else {
                currentSlot
            }
        }

        if (isAlreadySelected) {
            currentSelectedList.removeAll { it.id == slot.id }
        } else {
            currentSelectedList.add(slot.copy(status = TimeSlotStatus.SELECTED))
        }

        // Sắp xếp các ca đã chọn theo thứ tự thời gian tăng dần
        val sortedSelected = currentSelectedList.sortedWith(
            compareBy({ it.startHour }, { it.startMinute })
        )

        // Tính tổng tiền = Tổng đơn giá của tất cả các ca đã chọn
        val totalPrice = sortedSelected.sumOf { it.basePrice }

        _uiState.update { current ->
            current.copy(
                timeSlots = updatedSlots,
                selectedTimeSlots = sortedSelected,
                estimatedPrice = totalPrice,
                isConfirmEnabled = sortedSelected.isNotEmpty()
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
    // HELPER FUNCTIONS & OVERLAP DETECTION
    // ==========================================

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

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return days
    }

    private fun generateTimeSlots(
        day: CalendarDay,
        duration: SlotDuration,
        pricePerHour: Double,
        occupiedSlots: List<OccupiedSlot>
    ): List<TimeSlot> {
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMinute = now.get(Calendar.MINUTE)
        val currentTimeMinutes = currentHour * 60 + currentMinute

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
            val slotStartMinutes = t.startHour * 60 + t.startMinute
            val slotEndMinutes = slotStartMinutes + duration.minutes

            val isPast = if (day.isToday) {
                slotStartMinutes <= currentTimeMinutes
            } else {
                false
            }

            val isBooked = occupiedSlots.any { occupied ->
                val bookedStart = occupied.startMinutes
                val bookedEnd = occupied.endMinutes
                maxOf(slotStartMinutes, bookedStart) < minOf(slotEndMinutes, bookedEnd)
            }

            val status = when {
                isPast -> TimeSlotStatus.PAST
                isBooked -> TimeSlotStatus.BOOKED
                else -> TimeSlotStatus.AVAILABLE
            }

            TimeSlot(
                id = "slot_${duration.shortLabel}_${index}_${t.startHour}_${t.startMinute}",
                startTime = t.startTime,
                endTime = t.endTime,
                duration = duration,
                startHour = t.startHour,
                startMinute = t.startMinute,
                status = status,
                basePrice = calculatedPrice
            )
        }
    }

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
