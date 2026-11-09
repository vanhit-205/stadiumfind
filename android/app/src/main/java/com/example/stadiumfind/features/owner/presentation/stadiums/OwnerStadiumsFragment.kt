package com.example.stadiumfind.features.owner.presentation.stadiums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stadiumfind.R
import com.example.stadiumfind.databinding.FragmentOwnerStadiumsBinding
import com.example.stadiumfind.features.owner.presentation.adapter.OwnerStadiumAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OwnerStadiumsFragment : Fragment() {

    private var _binding: FragmentOwnerStadiumsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OwnerStadiumsViewModel by viewModels()
    private lateinit var adapter: OwnerStadiumAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOwnerStadiumsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = OwnerStadiumAdapter(
            onToggleStatus = { stadium, isChecked ->
                // Hiển thị dialog xác nhận trước khi đổi trạng thái
                val actionName = if (isChecked) "mở cửa" else "đóng cửa"
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Xác nhận trạng thái")
                    .setMessage("Bạn có chắc chắn muốn $actionName sân \"${stadium.name}\"?")
                    .setPositiveButton("Đồng ý") { _, _ ->
                        viewModel.toggleStadiumStatus(stadium, isChecked)
                    }
                    .setNegativeButton("Hủy") { dialog, _ ->
                        dialog.dismiss()
                        adapter.notifyDataSetChanged() // Revert switch state on cancel
                    }
                    .setOnCancelListener {
                        adapter.notifyDataSetChanged()
                    }
                    .show()
            },
            onViewRevenue = { stadium ->
                val bundle = bundleOf(
                    "stadiumId" to stadium.id,
                    "stadiumName" to stadium.name
                )
                findNavController().navigate(R.id.action_ownerStadiumsFragment_to_ownerRevenueFragment, bundle)
            }
        )

        binding.rvStadiums.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStadiums.adapter = adapter
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadOwnerStadiums()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        binding.swipeRefresh.isRefreshing = false
                        when (state) {
                            is OwnerStadiumsUiState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.layoutEmpty.visibility = View.GONE
                            }
                            is OwnerStadiumsUiState.Success -> {
                                binding.progressBar.visibility = View.GONE
                                adapter.submitList(state.stadiums)
                                binding.layoutEmpty.visibility = if (state.stadiums.isEmpty()) View.VISIBLE else View.GONE
                            }
                            is OwnerStadiumsUiState.Error -> {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            }
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
