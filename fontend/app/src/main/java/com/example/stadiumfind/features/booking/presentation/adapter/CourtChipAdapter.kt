package com.example.stadiumfind.features.booking.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stadiumfind.R
import com.example.stadiumfind.databinding.ItemCourtChipBinding
import com.example.stadiumfind.features.booking.domain.model.CourtItem

/**
 * Adapter hiển thị danh sách sân con (Court Chips).
 */
class CourtChipAdapter(
    private val onCourtClick: (CourtItem) -> Unit
) : ListAdapter<CourtItem, CourtChipAdapter.CourtChipViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourtChipViewHolder {
        val binding = ItemCourtChipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CourtChipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourtChipViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CourtChipViewHolder(
        private val binding: ItemCourtChipBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(court: CourtItem) {
            val context = itemView.context
            binding.tvCourtName.text = court.name

            if (court.isSelected) {
                binding.tvCourtName.setBackgroundResource(R.drawable.bg_chip_selected)
                binding.tvCourtName.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                binding.tvCourtName.setBackgroundResource(R.drawable.bg_chip_unselected)
                binding.tvCourtName.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
            }

            binding.tvCourtName.setOnClickListener {
                onCourtClick(court)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CourtItem>() {
        override fun areItemsTheSame(oldItem: CourtItem, newItem: CourtItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CourtItem, newItem: CourtItem): Boolean {
            return oldItem == newItem
        }
    }
}
