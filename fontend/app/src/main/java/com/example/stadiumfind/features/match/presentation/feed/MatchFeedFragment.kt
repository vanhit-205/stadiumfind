package com.example.stadiumfind.features.match.presentation.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stadiumfind.R
import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.databinding.FragmentMatchFeedBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MatchFeedFragment : Fragment() {

    private var _binding: FragmentMatchFeedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MatchFeedViewModel by activityViewModels()
    private lateinit var adapter: MatchFeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = MatchFeedAdapter(
            onJoinClick = { post ->
                Toast.makeText(requireContext(), "Yêu cầu tham gia kèo: ${post.title}", Toast.LENGTH_SHORT).show()
            },
            onItemClick = { post ->
                Toast.makeText(requireContext(), "Chi tiết: ${post.title}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvMatchPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMatchPosts.adapter = adapter
    }

    private fun setupListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadMatchPosts()
        }

        binding.btnRefreshFeed.setOnClickListener {
            binding.swipeRefreshLayout.isRefreshing = true
            viewModel.loadMatchPosts()
        }

        binding.fabCreateMatch.setOnClickListener {
            CreateMatchPostBottomSheet.newInstance().show(parentFragmentManager, CreateMatchPostBottomSheet.TAG)
        }

        // Filter chips
        val chips = listOf(
            binding.chipFilterAll to "ALL",
            binding.chipFilterFootball to "FOOTBALL",
            binding.chipFilterBadminton to "BADMINTON",
            binding.chipFilterTennis to "PICKLEBALL"
        )

        chips.forEach { (chip, sport) ->
            chip.setOnClickListener {
                updateFilterChipUi(chip, chips.map { it.first })
                viewModel.filterBySport(sport)
            }
        }
    }

    private fun updateFilterChipUi(selectedChip: TextView, allChips: List<TextView>) {
        allChips.forEach { chip ->
            if (chip == selectedChip) {
                chip.setBackgroundResource(R.drawable.bg_chip_selected)
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_primary))
            } else {
                chip.setBackgroundResource(R.drawable.bg_chip_unselected)
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        binding.swipeRefreshLayout.isRefreshing = false
                        when (state) {
                            is MatchFeedUiState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.layoutEmptyState.visibility = View.GONE
                            }
                            is MatchFeedUiState.Success -> {
                                binding.progressBar.visibility = View.GONE
                                adapter.submitList(state.posts)
                                if (state.posts.isEmpty()) {
                                    binding.layoutEmptyState.visibility = View.VISIBLE
                                } else {
                                    binding.layoutEmptyState.visibility = View.GONE
                                }
                            }
                            is MatchFeedUiState.Error -> {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                launch {
                    viewModel.createPostResult.collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                Toast.makeText(requireContext(), "Đăng bài tìm kèo thành công!", Toast.LENGTH_SHORT).show()
                                val bottomSheet = parentFragmentManager.findFragmentByTag(CreateMatchPostBottomSheet.TAG) as? CreateMatchPostBottomSheet
                                bottomSheet?.dismiss()
                            }
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
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
}
