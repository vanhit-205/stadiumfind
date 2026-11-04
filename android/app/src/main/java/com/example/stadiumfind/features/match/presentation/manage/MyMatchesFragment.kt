package com.example.stadiumfind.features.match.presentation.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stadiumfind.R
import com.example.stadiumfind.databinding.FragmentMyMatchesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyMatchesFragment : Fragment() {

    private var _binding: FragmentMyMatchesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyMatchesViewModel by viewModels()
    private lateinit var adapter: MyJoinedMatchesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyMatchesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeState()
    }

    private fun setupRecyclerView() {
        adapter = MyJoinedMatchesAdapter { participant ->
            showConfirmLeaveDialog(participant.matchPostId, participant.matchTitle)
        }
        binding.rvMyMatches.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMyMatches.adapter = adapter
    }

    private fun showConfirmLeaveDialog(matchPostId: Long, matchTitle: String?) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận rút lui")
            .setMessage("Bạn có chắc chắn muốn huỷ tham gia kèo \"${matchTitle ?: "này"}\"?")
            .setPositiveButton("Xác nhận") { _, _ ->
                viewModel.leaveMatch(matchPostId)
            }
            .setNegativeButton("Đóng", null)
            .show()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnExploreFeed.setOnClickListener {
            findNavController().navigate(R.id.matchFeedFragment)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadMyMatches()
        }

        // Filter chips
        val chips = listOf(
            binding.chipFilterAll to null,
            binding.chipFilterPending to "PENDING",
            binding.chipFilterAccepted to "ACCEPTED",
            binding.chipFilterRejected to "REJECTED"
        )

        chips.forEach { (chipView, status) ->
            chipView.setOnClickListener {
                updateChipSelection(chips.map { it.first }, chipView)
                viewModel.filterByStatus(status)
            }
        }
    }

    private fun updateChipSelection(allChips: List<TextView>, selectedChip: TextView) {
        val context = requireContext()
        allChips.forEach { chip ->
            if (chip == selectedChip) {
                chip.setBackgroundResource(R.drawable.bg_chip_selected)
                chip.setTextColor(ContextCompat.getColor(context, R.color.text_on_primary))
            } else {
                chip.setBackgroundResource(R.drawable.bg_chip_unselected)
                chip.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.progressBar.visibility = if (state.isLoading && state.matches.isEmpty()) View.VISIBLE else View.GONE

                        if (state.matches.isEmpty() && !state.isLoading) {
                            binding.layoutEmpty.visibility = View.VISIBLE
                            binding.rvMyMatches.visibility = View.GONE
                            val statusName = when (state.selectedStatus) {
                                "PENDING" -> "đang chờ duyệt"
                                "ACCEPTED" -> "đã được duyệt"
                                "REJECTED" -> "bị từ chối"
                                else -> ""
                            }
                            binding.tvEmptyTitle.text = if (statusName.isNotBlank()) "Không có kèo nào $statusName" else "Bạn chưa tham gia kèo nào"
                        } else {
                            binding.layoutEmpty.visibility = View.GONE
                            binding.rvMyMatches.visibility = View.VISIBLE
                            adapter.submitList(state.matches)
                        }
                    }
                }

                launch {
                    viewModel.toastEvent.collect { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
