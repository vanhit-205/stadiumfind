package com.example.stadiumfind.features.match.presentation.feed

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.stadiumfind.R
import com.example.stadiumfind.databinding.BottomSheetCreateMatchPostBinding
import com.example.stadiumfind.features.booking.domain.model.CourtItem
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.util.Calendar

class CreateMatchPostBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetCreateMatchPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MatchFeedViewModel by activityViewModels()

    private var currentStadiums: List<StadiumItem> = emptyList()
    private var currentCourts: List<CourtItem> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCreateMatchPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDatePicker()
        setupTimePickers()
        setupSpinners()
        setupSubmitButton()
        observeDialogState()

        // Đảm bảo luôn kích hoạt tải danh sách sân khi mở Dialog
        viewModel.loadStadiumsForCreate()
    }

    private fun setupSpinners() {
        binding.spStadium.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position in currentStadiums.indices) {
                    val selected = currentStadiums[position]
                    viewModel.selectStadium(selected)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        binding.spCourt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position in currentCourts.indices) {
                    viewModel.selectCourt(currentCourts[position])
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun observeDialogState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dialogState.collect { state ->
                    // Update stadiums
                    if (state.stadiums != currentStadiums && state.stadiums.isNotEmpty()) {
                        currentStadiums = state.stadiums
                        val names = state.stadiums.map { it.name }
                        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner_text, names)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spStadium.adapter = adapter
                    }

                    // Update courts
                    if (state.courts != currentCourts) {
                        currentCourts = state.courts
                        val courtNames = if (state.courts.isNotEmpty()) {
                            state.courts.map { "${it.name} (${it.sportType})" }
                        } else {
                            listOf("Đang tải sân con...")
                        }
                        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner_text, courtNames)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.spCourt.adapter = adapter
                    }

                    binding.btnSubmitMatchPost.setLoading(state.isSubmitting)
                }
            }
        }
    }

    private fun setupDatePicker() {
        binding.etPlayDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                    binding.etPlayDate.setText(formattedDate)
                },
                year,
                month,
                day
            )
            dpd.datePicker.minDate = System.currentTimeMillis()
            dpd.show()
        }
    }

    private fun setupTimePickers() {
        binding.etStartTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    binding.etStartTime.setText(String.format("%02d:%02d", hourOfDay, minute))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        binding.etEndTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    binding.etEndTime.setText(String.format("%02d:%02d", hourOfDay, minute))
                },
                calendar.get(Calendar.HOUR_OF_DAY) + 1,
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun setupSubmitButton() {
        binding.btnSubmitMatchPost.setOnClickListener {
            val title = binding.etMatchTitle.text.toString().trim()
            val playDate = binding.etPlayDate.text.toString().trim()
            val startTime = binding.etStartTime.text.toString().trim()
            val endTime = binding.etEndTime.text.toString().trim()
            val maxParticipantsStr = binding.etMaxParticipants.text.toString().trim()
            val priceStr = binding.etPricePerPerson.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập tiêu đề bài đăng", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (playDate.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng chọn ngày thi đấu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng chọn khung giờ thi đấu (Từ - Đến)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sportType = when (binding.rgSportType.checkedRadioButtonId) {
                R.id.rbBadminton -> "BADMINTON"
                R.id.rbPickleball -> "PICKLEBALL"
                else -> "FOOTBALL"
            }

            val maxParticipants = maxParticipantsStr.toIntOrNull() ?: 10
            val pricePerPerson = priceStr.toDoubleOrNull() ?: 0.0

            viewModel.createMatchPost(
                title = title,
                description = description,
                sportType = sportType,
                playDate = playDate,
                startTime = startTime,
                endTime = endTime,
                maxParticipants = maxParticipants,
                pricePerPerson = pricePerPerson
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "CreateMatchPostBottomSheet"
        fun newInstance() = CreateMatchPostBottomSheet()
    }
}
