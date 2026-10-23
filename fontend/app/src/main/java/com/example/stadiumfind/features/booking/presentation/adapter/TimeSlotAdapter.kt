package com.example.stadiumfind.features.booking.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stadiumfind.R
import com.example.stadiumfind.databinding.ItemTimeSlotBinding
import com.example.stadiumfind.features.booking.domain.model.TimeSlot
import com.example.stadiumfind.features.booking.domain.model.TimeSlotStatus

/**
 * Adapter hiển thị lưới khung giờ (Time Slot Grid).
 */
class TimeSlotAdapter(
    private val onSlotClick: (TimeSlot) -> Unit
) : ListAdapter<TimeSlot, TimeSlotAdapter.TimeSlotViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val binding = ItemTimeSlotBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TimeSlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TimeSlotViewHolder(
        private val binding: ItemTimeSlotBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(slot: TimeSlot) {
            val context = itemView.context
            binding.tvTimeRange.text = slot.displayRange

            when (slot.status) {
                TimeSlotStatus.SELECTED -> {
                    binding.cardTimeSlot.setBackgroundResource(R.drawable.bg_time_slot_selected)
                    binding.cardTimeSlot.isClickable = true
                    binding.cardTimeSlot.isFocusable = true
                    binding.ivCheckSelected.visibility = View.VISIBLE
                    binding.tvTimeRange.setTextColor(ContextCompat.getColor(context, R.color.white))
                    binding.tvSlotPriceOrStatus.text = slot.formattedPrice
                    binding.tvSlotPriceOrStatus.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
                TimeSlotStatus.AVAILABLE -> {
                    binding.cardTimeSlot.setBackgroundResource(R.drawable.bg_time_slot_available)
                    binding.cardTimeSlot.isClickable = true
                    binding.cardTimeSlot.isFocusable = true
                    binding.ivCheckSelected.visibility = View.GONE
                    binding.tvTimeRange.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                    binding.tvSlotPriceOrStatus.text = slot.formattedPrice
                    binding.tvSlotPriceOrStatus.setTextColor(ContextCompat.getColor(context, R.color.primary_300))
                }
                TimeSlotStatus.PAST -> {
                    binding.cardTimeSlot.setBackgroundResource(R.drawable.bg_time_slot_disabled)
                    binding.cardTimeSlot.isClickable = false
                    binding.cardTimeSlot.isFocusable = false
                    binding.ivCheckSelected.visibility = View.GONE
                    binding.tvTimeRange.setTextColor(ContextCompat.getColor(context, R.color.text_hint))
                    binding.tvSlotPriceOrStatus.text = "Quá giờ"
                    binding.tvSlotPriceOrStatus.setTextColor(ContextCompat.getColor(context, R.color.text_hint))
                }
                TimeSlotStatus.BOOKED -> {
                    binding.cardTimeSlot.setBackgroundResource(R.drawable.bg_time_slot_disabled)
                    binding.cardTimeSlot.isClickable = false
                    binding.cardTimeSlot.isFocusable = false
                    binding.ivCheckSelected.visibility = View.GONE
                    binding.tvTimeRange.setTextColor(ContextCompat.getColor(context, R.color.text_hint))
                    binding.tvSlotPriceOrStatus.text = "Đã đặt"
                    binding.tvSlotPriceOrStatus.setTextColor(ContextCompat.getColor(context, R.color.accent_orange))
                }
            }

            binding.cardTimeSlot.setOnClickListener {
                if (slot.status.isSelectable) {
                    onSlotClick(slot)
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<TimeSlot>() {
        override fun areItemsTheSame(oldItem: TimeSlot, newItem: TimeSlot): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TimeSlot, newItem: TimeSlot): Boolean {
            return oldItem == newItem
        }
    }
}
