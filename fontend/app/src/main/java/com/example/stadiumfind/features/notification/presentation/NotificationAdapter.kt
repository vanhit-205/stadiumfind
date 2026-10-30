package com.example.stadiumfind.features.notification.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stadiumfind.R
import com.example.stadiumfind.databinding.ItemNotificationBinding
import com.example.stadiumfind.features.notification.domain.model.NotificationItem

class NotificationAdapter(
    private val onItemClick: (NotificationItem) -> Unit
) : ListAdapter<NotificationItem, NotificationAdapter.NotificationViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NotificationViewHolder(
        private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(item: NotificationItem) {
            binding.tvTitle.text = item.title
            binding.tvContent.text = item.content
            binding.tvTime.text = item.createdAt?.take(16)?.replace("T", " ") ?: ""

            // Emoji theo loại thông báo
            binding.tvNotificationEmoji.text = when {
                item.type.contains("MATCH", ignoreCase = true) -> "⚽"
                item.type.contains("BOOKING", ignoreCase = true) -> "🎟️"
                else -> "🔔"
            }

            // Dot chấm xanh hiển thị nếu chưa đọc
            binding.viewUnreadDot.visibility = if (item.isRead) View.GONE else View.VISIBLE

            // Màu nền khác một chút khi chưa đọc
            val context = binding.root.context
            if (!item.isRead) {
                binding.cardNotification.strokeColor = context.getColor(R.color.primary_400)
            } else {
                binding.cardNotification.strokeColor = context.getColor(R.color.stroke_default)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<NotificationItem>() {
        override fun areItemsTheSame(oldItem: NotificationItem, newItem: NotificationItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NotificationItem, newItem: NotificationItem): Boolean {
            return oldItem == newItem
        }
    }
}
