package com.example.stadiumfind.features.match.presentation.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.databinding.BottomSheetManageMatchBinding
import com.example.stadiumfind.features.match.domain.model.MatchPost
import com.example.stadiumfind.features.match.presentation.feed.MatchFeedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class ManageMatchBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetManageMatchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MatchFeedViewModel by activityViewModels()
    private lateinit var adapter: ParticipantManageAdapter

    private var matchPostId: Long = 0L
    private var matchTitle: String = ""
    private var currentParticipants: Int = 0
    private var maxParticipants: Int = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            matchPostId = it.getLong(ARG_ID)
            matchTitle = it.getString(ARG_TITLE, "")
            currentParticipants = it.getInt(ARG_CURRENT_PARTICIPANTS, 0)
            maxParticipants = it.getInt(ARG_MAX_PARTICIPANTS, 10)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetManageMatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateHeaderInfo()
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        viewModel.loadParticipants(matchPostId)
    }

    private fun updateHeaderInfo() {
        binding.tvManageSubtitle.text = "$matchTitle (Hiện có: $currentParticipants/$maxParticipants thành viên)"
    }

    private fun setupRecyclerView() {
        adapter = ParticipantManageAdapter(
            onAcceptClick = { participant ->
                viewModel.updateParticipantStatus(matchPostId, participant.id, "ACCEPTED")
            },
            onRejectClick = { participant ->
                viewModel.updateParticipantStatus(matchPostId, participant.id, "REJECTED")
            }
        )
        binding.rvParticipants.layoutManager = LinearLayoutManager(requireContext())
        binding.rvParticipants.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnCloseManage.setOnClickListener {
            dismiss()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.manageMatchState.collect { state ->
                        binding.pbLoadingParticipants.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                        adapter.submitList(state.participants)

                        if (!state.isLoading && state.participants.isEmpty()) {
                            binding.layoutEmptyParticipants.visibility = View.VISIBLE
                        } else {
                            binding.layoutEmptyParticipants.visibility = View.GONE
                        }

                        if (!state.errorMessage.isNullOrBlank()) {
                            Toast.makeText(requireContext(), state.errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                launch {
                    viewModel.updateParticipantResult.collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                Toast.makeText(requireContext(), "Cập nhật trạng thái thành công!", Toast.LENGTH_SHORT).show()
                            }
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), "Lỗi: ${result.message}", Toast.LENGTH_LONG).show()
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ManageMatchBottomSheet"

        private const val ARG_ID = "id"
        private const val ARG_TITLE = "title"
        private const val ARG_CURRENT_PARTICIPANTS = "current_participants"
        private const val ARG_MAX_PARTICIPANTS = "max_participants"

        fun newInstance(post: MatchPost): ManageMatchBottomSheet {
            val fragment = ManageMatchBottomSheet()
            val bundle = Bundle().apply {
                putLong(ARG_ID, post.id)
                putString(ARG_TITLE, post.title)
                putInt(ARG_CURRENT_PARTICIPANTS, post.currentParticipants)
                putInt(ARG_MAX_PARTICIPANTS, post.maxParticipants)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}
