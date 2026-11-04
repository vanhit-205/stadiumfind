package com.example.stadiumfind.features.match.presentation.manage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stadiumfind.R
import com.example.stadiumfind.databinding.ItemMyJoinedMatchBinding
import com.example.stadiumfind.features.match.domain.model.MatchParticipant

class MyJoinedMatchesAdapter(
    private val onLeaveClick: (MatchParticipant) -> Unit
) : ListAdapter<MatchParticipant, MyJoinedMatchesAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyJoinedMatchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemMyJoinedMatchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MatchParticipant) {
            val context = binding.root.context

            binding.tvSportBadge.text = when (item.sportType?.uppercase()) {
                "FOOTBALL" -> "⚽ Bóng đá"
                "BADMINTON" -> "🏸 Cầu lông"
                "TENNIS" -> "🎾 Tennis"
                "PICKLEBALL" -> "🏓 Pickleball"
                else -> "⚽ ${item.sportType ?: "Thể thao"}"
            }

            // Trạng thái tham gia
            when {
                item.isAccepted -> {
                    binding.tvStatusBadge.text = "✅ Đã duyệt"
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.primary_300))
                    binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_open)
                    binding.btnLeaveMatch.text = "Rời kèo"
                    binding.btnLeaveMatch.visibility = View.VISIBLE
                }
                item.isPending -> {
                    binding.tvStatusBadge.text = "⏳ Chờ duyệt"
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.accent_yellow))
                    binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_rating)
                    binding.btnLeaveMatch.text = "Huỷ yêu cầu"
                    binding.btnLeaveMatch.visibility = View.VISIBLE
                }
                item.isRejected -> {
                    binding.tvStatusBadge.text = "❌ Bị từ chối"
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.error_red))
                    binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_full)
                    binding.btnLeaveMatch.visibility = View.GONE
                }
                item.isCancelled -> {
                    binding.tvStatusBadge.text = "🚫 Đã huỷ"
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.text_hint))
                    binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_full)
                    binding.btnLeaveMatch.visibility = View.GONE
                }
                else -> {
                    binding.tvStatusBadge.text = item.status
                    binding.btnLeaveMatch.visibility = View.GONE
                }
            }

            binding.tvMatchTitle.text = item.matchTitle ?: "Bài đăng tìm kèo #${item.matchPostId}"

            val court = item.courtName ?: ""
            val stadium = item.stadiumName ?: "Sân thể thao"
            binding.tvStadiumAndCourt.text = if (court.isNotBlank()) "$stadium - $court" else stadium

            val time = if (item.startTime != null && item.endTime != null) {
                "${item.startTime.take(5)} - ${item.endTime.take(5)}"
            } else ""
            val date = item.playDate ?: ""
            binding.tvDateTime.text = if (time.isNotBlank()) "$time | $date" else date

            if (!item.note.isNullOrBlank()) {
                binding.tvMyNote.visibility = View.VISIBLE
                binding.tvMyNote.text = "Lời nhắn: ${item.note}"
            } else {
                binding.tvMyNote.visibility = View.GONE
            }

            binding.tvParticipantsCount.text = "👥 ${item.currentParticipants}/${item.maxParticipants} người"

            binding.btnLeaveMatch.setOnClickListener {
                onLeaveClick(item)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MatchParticipant>() {
        override fun areItemsTheSame(oldItem: MatchParticipant, newItem: MatchParticipant): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MatchParticipant, newItem: MatchParticipant): Boolean {
            return oldItem == newItem
        }
    }
}
