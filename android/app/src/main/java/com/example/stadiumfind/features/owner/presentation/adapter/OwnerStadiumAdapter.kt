package com.example.stadiumfind.features.owner.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.stadiumfind.R
import com.example.stadiumfind.databinding.ItemOwnerStadiumBinding
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem

class OwnerStadiumAdapter(
    private val onToggleStatus: (StadiumItem, Boolean) -> Unit,
    private val onViewRevenue: (StadiumItem) -> Unit
) : ListAdapter<StadiumItem, OwnerStadiumAdapter.OwnerStadiumViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerStadiumViewHolder {
        val binding = ItemOwnerStadiumBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OwnerStadiumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OwnerStadiumViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OwnerStadiumViewHolder(
        private val binding: ItemOwnerStadiumBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(stadium: StadiumItem) {
            val context = binding.root.context

            binding.tvStadiumName.text = stadium.name
            binding.tvStadiumAddress.text = stadium.address
            binding.tvCourtCount.text = "Quy mô: ${stadium.totalCourts} sân con"

            // Ảnh sân
            binding.ivStadiumImage.load(stadium.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_stadium_placeholder)
                error(R.drawable.ic_stadium_placeholder)
            }

            // Trạng thái mở / đóng
            val isOpen = stadium.isActive
            updateBadgeUI(isOpen)

            // Switch (ngắt listener trước khi gán để tránh trigger vòng lặp)
            binding.switchStatus.setOnCheckedChangeListener(null)
            binding.switchStatus.isChecked = isOpen
            binding.switchStatus.setOnCheckedChangeListener { _, isChecked ->
                onToggleStatus(stadium, isChecked)
            }

            // Nút xem doanh thu
            binding.btnViewRevenue.setOnClickListener {
                onViewRevenue(stadium)
            }
        }

        private fun updateBadgeUI(isOpen: Boolean) {
            val context = binding.root.context
            if (isOpen) {
                binding.tvStatusBadge.text = "● ĐANG MỞ CỬA"
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.primary_300))
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_badge_success)
                binding.tvStatusSubtext.text = "Khách có thể tìm và đặt sân"
            } else {
                binding.tvStatusBadge.text = "● TẠM ĐÓNG CỬA"
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.error_red))
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_badge_closed)
                binding.tvStatusSubtext.text = "Tạm ngừng nhận đơn đặt sân mới"
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StadiumItem>() {
            override fun areItemsTheSame(oldItem: StadiumItem, newItem: StadiumItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: StadiumItem, newItem: StadiumItem): Boolean =
                oldItem == newItem
        }
    }
}
