package com.example.stadiumfind.features.booking.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stadiumfind.R
import com.example.stadiumfind.databinding.ItemCalendarDayBinding
import com.example.stadiumfind.features.booking.domain.model.CalendarDay

/**
 * Adapter cho thanh lịch ngang (Horizontal Calendar).
 */
class CalendarDayAdapter(
    private val onDayClick: (CalendarDay) -> Unit
) : ListAdapter<CalendarDay, CalendarDayAdapter.CalendarDayViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarDayViewHolder {
        val binding = ItemCalendarDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CalendarDayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarDayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CalendarDayViewHolder(
        private val binding: ItemCalendarDayBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(day: CalendarDay) {
            val context = itemView.context

            // Hiển thị Thứ (hoặc Hôm nay)
            binding.tvDayOfWeek.text = if (day.isToday) "Hôm nay" else day.dayOfWeek
            binding.tvDayOfMonth.text = day.dayOfMonth.toString()
            binding.tvMonth.text = "T${day.month}"

            if (day.isSelected) {
                // Trạng thái Đang chọn: Nền xanh Primary
                binding.cardCalendarDay.setBackgroundResource(R.drawable.bg_calendar_day_selected)
                binding.tvDayOfWeek.setTextColor(ContextCompat.getColor(context, R.color.white))
                binding.tvDayOfMonth.setTextColor(ContextCompat.getColor(context, R.color.white))
                binding.tvMonth.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                // Trạng thái Bình thường
                binding.cardCalendarDay.setBackgroundResource(R.drawable.bg_calendar_day_unselected)
                if (day.isToday) {
                    binding.tvDayOfWeek.setTextColor(ContextCompat.getColor(context, R.color.primary_300))
                } else {
                    binding.tvDayOfWeek.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
                }
                binding.tvDayOfMonth.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                binding.tvMonth.setTextColor(ContextCompat.getColor(context, R.color.text_hint))
            }

            binding.cardCalendarDay.setOnClickListener {
                onDayClick(day)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CalendarDay>() {
        override fun areItemsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
            return oldItem.dateMillis == newItem.dateMillis
        }

        override fun areContentsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
            return oldItem == newItem
        }
    }
}
