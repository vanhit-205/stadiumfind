package com.example.stadiumfind.features.match.presentation.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.stadiumfind.R
import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.databinding.BottomSheetJoinMatchBinding
import com.example.stadiumfind.features.match.domain.model.MatchPost
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class JoinMatchBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetJoinMatchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MatchFeedViewModel by activityViewModels()

    private var matchPost: MatchPost? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        matchPost = arguments?.let {
            MatchPost(
                id = it.getLong(ARG_ID),
                creatorId = it.getLong(ARG_CREATOR_ID),
                creatorName = it.getString(ARG_CREATOR_NAME, ""),
                creatorAvatar = it.getString(ARG_CREATOR_AVATAR),
                courtId = it.getLong(ARG_COURT_ID),
                courtName = it.getString(ARG_COURT_NAME, ""),
                stadiumId = it.getLong(ARG_STADIUM_ID),
                stadiumName = it.getString(ARG_STADIUM_NAME, ""),
                bookingId = null,
                title = it.getString(ARG_TITLE, ""),
                description = it.getString(ARG_DESC, ""),
                sportType = it.getString(ARG_SPORT, ""),
                playDate = it.getString(ARG_DATE, ""),
                startTime = it.getString(ARG_START_TIME, ""),
                endTime = it.getString(ARG_END_TIME, ""),
                maxParticipants = it.getInt(ARG_MAX_PARTICIPANTS, 10),
                currentParticipants = it.getInt(ARG_CURRENT_PARTICIPANTS, 1),
                pricePerPerson = it.getDouble(ARG_PRICE, 0.0),
                status = it.getString(ARG_STATUS, "OPEN")
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetJoinMatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateData()
        setupListeners()
        observeViewModel()
    }

    private fun populateData() {
        val post = matchPost ?: return

        binding.tvSummaryTitle.text = post.title
        binding.tvSummaryCreator.text = "Chủ kèo: ${post.creatorName}"
        binding.tvSummaryStadium.text = "${post.stadiumName} • ${post.courtName}"
        binding.tvSummaryTime.text = "${post.timeRangeFormatted} • ${post.playDate}"

        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        binding.tvSummaryPrice.text = "${formatter.format(post.pricePerPerson)} / người"
        binding.tvSummarySlots.text = "Còn ${post.remainingSlots} slot"
    }

    private fun setupListeners() {
        binding.btnCancelJoin.setOnClickListener {
            dismiss()
        }

        binding.btnConfirmJoin.setOnClickListener {
            val post = matchPost ?: return@setOnClickListener
            val note = binding.etJoinNote.text?.toString()?.trim()
            viewModel.joinMatch(post.id, note)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isJoining.collect { isJoining ->
                    binding.pbConfirmJoin.visibility = if (isJoining) View.VISIBLE else View.GONE
                    binding.btnConfirmJoin.isEnabled = !isJoining
                    binding.btnCancelJoin.isEnabled = !isJoining
                    binding.btnConfirmJoin.text = if (isJoining) "" else "Xác Nhận Tham Gia"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "JoinMatchBottomSheet"

        private const val ARG_ID = "id"
        private const val ARG_CREATOR_ID = "creator_id"
        private const val ARG_CREATOR_NAME = "creator_name"
        private const val ARG_CREATOR_AVATAR = "creator_avatar"
        private const val ARG_COURT_ID = "court_id"
        private const val ARG_COURT_NAME = "court_name"
        private const val ARG_STADIUM_ID = "stadium_id"
        private const val ARG_STADIUM_NAME = "stadium_name"
        private const val ARG_TITLE = "title"
        private const val ARG_DESC = "desc"
        private const val ARG_SPORT = "sport"
        private const val ARG_DATE = "date"
        private const val ARG_START_TIME = "start_time"
        private const val ARG_END_TIME = "end_time"
        private const val ARG_MAX_PARTICIPANTS = "max_participants"
        private const val ARG_CURRENT_PARTICIPANTS = "current_participants"
        private const val ARG_PRICE = "price"
        private const val ARG_STATUS = "status"

        fun newInstance(post: MatchPost): JoinMatchBottomSheet {
            val fragment = JoinMatchBottomSheet()
            val bundle = Bundle().apply {
                putLong(ARG_ID, post.id)
                putLong(ARG_CREATOR_ID, post.creatorId)
                putString(ARG_CREATOR_NAME, post.creatorName)
                putString(ARG_CREATOR_AVATAR, post.creatorAvatar)
                putLong(ARG_COURT_ID, post.courtId ?: 0L)
                putString(ARG_COURT_NAME, post.courtName)
                putLong(ARG_STADIUM_ID, post.stadiumId ?: 0L)
                putString(ARG_STADIUM_NAME, post.stadiumName)
                putString(ARG_TITLE, post.title)
                putString(ARG_DESC, post.description)
                putString(ARG_SPORT, post.sportType)
                putString(ARG_DATE, post.playDate)
                putString(ARG_START_TIME, post.startTime)
                putString(ARG_END_TIME, post.endTime)
                putInt(ARG_MAX_PARTICIPANTS, post.maxParticipants)
                putInt(ARG_CURRENT_PARTICIPANTS, post.currentParticipants)
                putDouble(ARG_PRICE, post.pricePerPerson)
                putString(ARG_STATUS, post.status)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}
