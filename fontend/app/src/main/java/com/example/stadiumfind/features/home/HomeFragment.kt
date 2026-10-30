package com.example.stadiumfind.features.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stadiumfind.R
import com.example.stadiumfind.databinding.FragmentHomeBinding
import com.example.stadiumfind.features.stadium.presentation.adapter.StadiumAdapter
import com.example.stadiumfind.features.stadium.presentation.adapter.StadiumLoadStateAdapter
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.stadiumfind.features.stadium.presentation.detail.StadiumDetailFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var stadiumAdapter: StadiumAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        stadiumAdapter = StadiumAdapter(
            onItemClick = { stadium ->
                val bundle = bundleOf(StadiumDetailFragment.ARG_STADIUM_ID to stadium.id)
                findNavController().navigate(R.id.action_homeFragment_to_stadiumDetailFragment, bundle)
            },
            onBookClick = { stadium ->
                val bundle = bundleOf(StadiumDetailFragment.ARG_STADIUM_ID to stadium.id)
                findNavController().navigate(R.id.action_homeFragment_to_stadiumDetailFragment, bundle)
            }
        )

        binding.rvStadiums.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = stadiumAdapter.withLoadStateHeaderAndFooter(
                header = StadiumLoadStateAdapter { stadiumAdapter.retry() },
                footer = StadiumLoadStateAdapter { stadiumAdapter.retry() }
            )
            setHasFixedSize(false)
        }
    }

    private fun setupListeners() {
        // Swipe to Refresh
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.primary_400)
        binding.swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.surface_card)
        binding.swipeRefreshLayout.setOnRefreshListener {
            stadiumAdapter.refresh()
        }

        // Retry ban đầu nếu lỗi kết nối
        binding.btnRetryInitial.setOnClickListener {
            stadiumAdapter.retry()
        }

        // Thanh tìm kiếm nhanh
        binding.layoutSearchBar.setOnClickListener {
            Toast.makeText(requireContext(), "Chuyển sang màn hình tìm kiếm sân", Toast.LENGTH_SHORT).show()
        }

        binding.btnNotification.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
        }

        binding.btnBannerAction.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_matchFeedFragment)
        }

        binding.tvViewAll.setOnClickListener {
            viewModel.setFilter(keyword = null, provinceCity = null)
        }

        // Chip bộ lọc nhanh
        binding.chipAll.setOnClickListener {
            updateChipSelection(binding.chipAll)
            viewModel.setFilter(keyword = null, provinceCity = null)
        }
        binding.chipNear.setOnClickListener {
            updateChipSelection(binding.chipNear)
            viewModel.setFilter(keyword = null, provinceCity = "Hà Nội")
        }
        binding.chipHighRated.setOnClickListener {
            updateChipSelection(binding.chipHighRated)
            viewModel.setFilter(keyword = null, provinceCity = null)
        }
        binding.chipCourt7.setOnClickListener {
            updateChipSelection(binding.chipCourt7)
            viewModel.setFilter(keyword = "Sân 7", provinceCity = null)
        }
        binding.chipCourt5.setOnClickListener {
            updateChipSelection(binding.chipCourt5)
            viewModel.setFilter(keyword = "Sân 5", provinceCity = null)
        }
    }

    private fun updateChipSelection(selectedView: View) {
        val chips = listOf(
            binding.chipAll,
            binding.chipNear,
            binding.chipHighRated,
            binding.chipCourt7,
            binding.chipCourt5
        )
        for (chip in chips) {
            if (chip == selectedView) {
                chip.setBackgroundResource(R.drawable.bg_chip_selected)
                chip.setTextColor(requireContext().getColor(R.color.text_on_primary))
            } else {
                chip.setBackgroundResource(R.drawable.bg_chip_unselected)
                chip.setTextColor(requireContext().getColor(R.color.text_secondary))
            }
        }
    }

    private fun observeViewModel() {
        // Quan sát PagingData phân trang
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stadiumsFlow.collectLatest { pagingData ->
                    stadiumAdapter.submitData(pagingData)
                }
            }
        }

        // Quan sát trạng thái phân trang (LoadState: Loading, Error, Empty)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                stadiumAdapter.loadStateFlow.collectLatest { loadStates ->
                    val refreshState = loadStates.refresh
                    val isInitialLoading = refreshState is LoadState.Loading
                    val isInitialError = refreshState is LoadState.Error && stadiumAdapter.itemCount == 0
                    val isListEmpty = refreshState is LoadState.NotLoading && stadiumAdapter.itemCount == 0

                    // Dừng biểu tượng xoay SwipeRefreshLayout
                    binding.swipeRefreshLayout.isRefreshing = isInitialLoading && stadiumAdapter.itemCount > 0

                    // Hiển thị ProgressBar nếu lần đầu tải và chưa có item nào
                    binding.progressBarLoading.isVisible = isInitialLoading && stadiumAdapter.itemCount == 0

                    // Hiển thị giao diện lỗi nếu kết nối thất bại
                    binding.layoutError.isVisible = isInitialError
                    if (refreshState is LoadState.Error) {
                        binding.tvErrorMessage.text = refreshState.error.localizedMessage
                            ?: "Không thể kết nối đến máy chủ. Vui lòng kiểm tra lại!"
                    }

                    // Hiển thị thông báo rỗng nếu không có dữ liệu
                    binding.layoutEmpty.isVisible = isListEmpty
                    binding.rvStadiums.isVisible = !isInitialError && !isListEmpty
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
