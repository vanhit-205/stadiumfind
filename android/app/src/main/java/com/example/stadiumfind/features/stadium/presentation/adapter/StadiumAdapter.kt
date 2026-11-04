package com.example.stadiumfind.features.stadium.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.stadiumfind.core.util.loadStadiumImage
import com.example.stadiumfind.databinding.ItemStadiumBinding
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import java.util.Locale

/**
 * Adapter hiển thị danh sách item sân bóng trên RecyclerView có hỗ trợ Paging 3.
 * Kế thừa PagingDataAdapter với DiffUtil tối ưu hiệu năng render & animation.
 */
class StadiumAdapter(
    private val onItemClick: (StadiumItem) -> Unit = {},
    private val onBookClick: (StadiumItem) -> Unit = {}
) : PagingDataAdapter<StadiumItem, StadiumAdapter.StadiumViewHolder>(StadiumDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StadiumViewHolder {
        val binding = ItemStadiumBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StadiumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StadiumViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class StadiumViewHolder(
        private val binding: ItemStadiumBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(stadium: StadiumItem) {
            binding.apply {
                // Tên sân & Thông tin cơ bản
                tvStadiumName.text = stadium.name
                tvStadiumAddress.text = stadium.locationShort
                tvOperatingHours.text = stadium.formattedOperatingHours
                tvSportBadge.text = stadium.primarySportBadge
                tvRating.text = String.format(Locale.US, "%.1f", stadium.rating)
                tvPrice.text = stadium.formattedPrice

                // Tải ảnh sân qua Coil extension
                ivStadium.loadStadiumImage(stadium.imageUrl)

                // Sự kiện click
                cardStadium.setOnClickListener {
                    onItemClick(stadium)
                }

                btnBookNow.setOnClickListener {
                    onBookClick(stadium)
                }
            }
        }
    }

    companion object StadiumDiffCallback : DiffUtil.ItemCallback<StadiumItem>() {
        override fun areItemsTheSame(oldItem: StadiumItem, newItem: StadiumItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StadiumItem, newItem: StadiumItem): Boolean {
            return oldItem == newItem
        }
    }
}
