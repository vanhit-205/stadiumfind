package com.example.stadiumfind.features.match.presentation.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.booking.domain.model.CourtItem
import com.example.stadiumfind.features.match.data.remote.dto.MatchPostCreateRequestDto
import com.example.stadiumfind.features.match.domain.model.MatchPost
import com.example.stadiumfind.features.match.domain.repository.MatchRepository
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MatchFeedUiState {
    object Loading : MatchFeedUiState
    data class Success(val posts: List<MatchPost>) : MatchFeedUiState
    data class Error(val message: String) : MatchFeedUiState
}

data class CreateMatchDialogState(
    val stadiums: List<StadiumItem> = emptyList(),
    val selectedStadium: StadiumItem? = null,
    val courts: List<CourtItem> = emptyList(),
    val selectedCourt: CourtItem? = null,
    val isCourtsLoading: Boolean = false,
    val isSubmitting: Boolean = false
)

@HiltViewModel
class MatchFeedViewModel @Inject constructor(
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MatchFeedUiState>(MatchFeedUiState.Loading)
    val uiState: StateFlow<MatchFeedUiState> = _uiState.asStateFlow()

    private val _createPostResult = MutableSharedFlow<Resource<MatchPost>>()
    val createPostResult: SharedFlow<Resource<MatchPost>> = _createPostResult.asSharedFlow()

    private val _dialogState = MutableStateFlow(CreateMatchDialogState())
    val dialogState: StateFlow<CreateMatchDialogState> = _dialogState.asStateFlow()

    private var allPosts: List<MatchPost> = emptyList()
    private var currentSportFilter: String = "ALL"

    init {
        loadMatchPosts()
        loadStadiumsForCreate()
    }

    fun loadMatchPosts() {
        viewModelScope.launch {
            matchRepository.getOpenMatchPosts(currentSportFilter).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        if (allPosts.isEmpty()) {
                            _uiState.value = MatchFeedUiState.Loading
                        }
                    }
                    is Resource.Success -> {
                        allPosts = result.data
                        applyFilter(currentSportFilter)
                    }
                    is Resource.Error -> {
                        _uiState.value = MatchFeedUiState.Error(result.message)
                    }
                }
            }
        }
    }

    fun filterBySport(sportType: String) {
        currentSportFilter = sportType
        loadMatchPosts()
    }

    private fun applyFilter(sportType: String) {
        val filtered = if (sportType == "ALL") {
            allPosts
        } else {
            allPosts.filter { it.sportType.equals(sportType, ignoreCase = true) }
        }
        _uiState.value = MatchFeedUiState.Success(filtered)
    }

    fun loadStadiumsForCreate() {
        viewModelScope.launch {
            when (val res = matchRepository.getStadiums()) {
                is Resource.Success -> {
                    val stadiums = res.data
                    val firstStadium = stadiums.firstOrNull()
                    _dialogState.update { it.copy(stadiums = stadiums, selectedStadium = firstStadium) }
                    if (firstStadium != null) {
                        loadCourtsForStadium(firstStadium.id)
                    }
                }
                else -> Unit
            }
        }
    }

    fun selectStadium(stadium: StadiumItem) {
        _dialogState.update { it.copy(selectedStadium = stadium, selectedCourt = null) }
        loadCourtsForStadium(stadium.id)
    }

    fun selectCourt(court: CourtItem) {
        _dialogState.update { it.copy(selectedCourt = court) }
    }

    private fun loadCourtsForStadium(stadiumId: Long) {
        viewModelScope.launch {
            _dialogState.update { it.copy(isCourtsLoading = true) }
            when (val res = matchRepository.getCourtsByStadium(stadiumId)) {
                is Resource.Success -> {
                    val courts = res.data
                    _dialogState.update {
                        it.copy(
                            courts = courts,
                            selectedCourt = courts.firstOrNull(),
                            isCourtsLoading = false
                        )
                    }
                }
                is Resource.Error -> {
                    _dialogState.update { it.copy(courts = emptyList(), selectedCourt = null, isCourtsLoading = false) }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun createMatchPost(
        title: String,
        description: String,
        sportType: String,
        playDate: String,
        startTime: String,
        endTime: String,
        maxParticipants: Int,
        pricePerPerson: Double
    ) {
        val courtId = _dialogState.value.selectedCourt?.id
        if (courtId == null) {
            viewModelScope.launch {
                _createPostResult.emit(Resource.Error("Vui lòng chọn cụm sân và sân con hợp lệ"))
            }
            return
        }

        val request = MatchPostCreateRequestDto(
            courtId = courtId,
            title = title,
            description = description,
            sportType = sportType,
            playDate = playDate,
            startTime = if (startTime.length == 5) "$startTime:00" else startTime,
            endTime = if (endTime.length == 5) "$endTime:00" else endTime,
            maxParticipants = maxParticipants,
            pricePerPerson = pricePerPerson
        )

        viewModelScope.launch {
            _dialogState.update { it.copy(isSubmitting = true) }
            matchRepository.createMatchPost(request).collect { result ->
                if (result !is Resource.Loading) {
                    _dialogState.update { it.copy(isSubmitting = false) }
                }
                _createPostResult.emit(result)
                if (result is Resource.Success) {
                    loadMatchPosts()
                }
            }
        }
    }
}
