package com.example.stadiumfind.features.stadium.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stadiumfind.databinding.ItemLoadStateBinding

/**
 * LoadStateAdapter quản lý hiển thị trạng thái Loading và Error kèm nút Retry
 * ở đầu (Header) hoặc cuối (Footer) danh sách phân trang Paging 3.
 */
class StadiumLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<StadiumLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = ItemLoadStateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LoadStateViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class LoadStateViewHolder(
        private val binding: ItemLoadStateBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnLoadStateRetry.setOnClickListener { retry() }
        }

        fun bind(loadState: LoadState) {
            binding.progressBarLoadState.isVisible = loadState is LoadState.Loading
            binding.tvLoadStateError.isVisible = loadState is LoadState.Error
            binding.btnLoadStateRetry.isVisible = loadState is LoadState.Error

            if (loadState is LoadState.Error) {
                val errorMsg = loadState.error.localizedMessage ?: "Có lỗi xảy ra khi tải thêm dữ liệu"
                binding.tvLoadStateError.text = errorMsg
            }
        }
    }
}
