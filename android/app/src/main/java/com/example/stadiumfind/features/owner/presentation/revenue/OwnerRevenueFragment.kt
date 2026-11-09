package com.example.stadiumfind.features.owner.presentation.revenue

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stadiumfind.databinding.FragmentOwnerRevenueBinding
import com.example.stadiumfind.features.owner.presentation.adapter.RevenuePeriodAdapter
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class OwnerRevenueFragment : Fragment() {

    private var _binding: FragmentOwnerRevenueBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OwnerRevenueViewModel by viewModels()
    private lateinit var adapter: RevenuePeriodAdapter

    private val currencyFormatter: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale("vi", "VN")).apply {
            groupingSeparator = ','
        }
        DecimalFormat("#,###", symbols)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOwnerRevenueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupTabs()
        setupFilterButton()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        if (!viewModel.stadiumName.isNullOrEmpty()) {
            binding.toolbar.subtitle = viewModel.stadiumName
        } else {
            binding.toolbar.subtitle = "Tất cả các sân của bạn"
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val isMonthly = tab?.position == 1
                viewModel.setPeriodType(isMonthly)
                updateFilterLabel()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
            override fun onTabReselected(tab: TabLayout.Tab?) = Unit
        })
    }

    private fun setupFilterButton() {
        updateFilterLabel()
        binding.btnChangeFilter.setOnClickListener {
            val isMonthly = viewModel.selectedType == "MONTHLY"
            val calendar = Calendar.getInstance()
            val year = viewModel.selectedYear
            val month = viewModel.selectedMonth - 1

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonthIndex, _ ->
                    viewModel.setDateFilter(selectedYear, selectedMonthIndex + 1)
                    updateFilterLabel()
                },
                year,
                month,
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.setTitle(if (isMonthly) "Chọn năm" else "Chọn tháng & năm")
            datePickerDialog.show()
        }
    }

    private fun updateFilterLabel() {
        if (viewModel.selectedType == "MONTHLY") {
            binding.tvCurrentFilterLabel.text = "Thời gian: Năm ${viewModel.selectedYear}"
        } else {
            binding.tvCurrentFilterLabel.text = "Thời gian: Tháng ${viewModel.selectedMonth}/${viewModel.selectedYear}"
        }
    }

    private fun setupRecyclerView() {
        adapter = RevenuePeriodAdapter()
        binding.rvRevenueItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRevenueItems.adapter = adapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is OwnerRevenueUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.tvEmptyDetail.visibility = View.GONE
                        }
                        is OwnerRevenueUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val revenueData = state.revenue
                            val formattedTotal = currencyFormatter.format(revenueData.totalRevenue) + " đ"
                            binding.tvTotalRevenue.text = formattedTotal
                            binding.tvTotalBookings.text = "${revenueData.totalBookings} lượt"

                            // Filter out 0 revenue items if desired, or show all
                            adapter.submitList(revenueData.items)
                            binding.tvEmptyDetail.visibility =
                                if (revenueData.items.isEmpty()) View.VISIBLE else View.GONE
                        }
                        is OwnerRevenueUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
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
