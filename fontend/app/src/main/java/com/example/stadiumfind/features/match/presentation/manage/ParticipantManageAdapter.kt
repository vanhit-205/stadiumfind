package com.example.stadiumfind.features.match.presentation.manage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.stadiumfind.R
import com.example.stadiumfind.databinding.ItemManageParticipantBinding
import com.example.stadiumfind.features.match.domain.model.MatchParticipant

class ParticipantManageAdapter(
    private val onAcceptClick: (MatchParticipant) -> Unit,
    private val onRejectClick: (MatchParticipant) -> Unit
) : ListAdapter<MatchParticipant, ParticipantManageAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(
        private val binding: ItemManageParticipantBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MatchParticipant) {
            val context = binding.root.context

            binding.tvParticipantName.text = item.userName
            binding.tvParticipantPhone.text = if (!item.userPhoneNumber.isNullOrBlank()) {
                "SĐT: ${item.userPhoneNumber}"
            } else {
                "SĐT: Chưa cập nhật"
            }

            binding.ivParticipantAvatar.load(item.userAvatar) {
                crossfade(true)
                placeholder(R.drawable.ic_person)
                error(R.drawable.ic_person)
                transformations(CircleCropTransformation())
            }

            if (!item.note.isNullOrBlank()) {
                binding.tvParticipantNote.visibility = View.VISIBLE
                binding.tvParticipantNote.text = "Lời nhắn: ${item.note}"
            } else {
                binding.tvParticipantNote.visibility = View.GONE
            }

            // Trạng thái & Hiển thị nút bấm
            when {
                item.isAccepted -> {
                    binding.tvParticipantStatusBadge.text = "Đã duyệt"
                    binding.tvParticipantStatusBadge.setBackgroundResource(R.drawable.bg_badge_open)
                    binding.tvParticipantStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.primary_300))
                    binding.layoutActionButtons.visibility = View.GONE
                }
                item.isRejected -> {
                    binding.tvParticipantStatusBadge.text = "Từ chối"
                    binding.tvParticipantStatusBadge.setBackgroundResource(R.drawable.bg_badge_full)
                    binding.tvParticipantStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.error_red))
                    binding.layoutActionButtons.visibility = View.GONE
                }
                else -> {
                    binding.tvParticipantStatusBadge.text = "Chờ duyệt"
                    binding.tvParticipantStatusBadge.setBackgroundResource(R.drawable.bg_badge_open)
                    binding.tvParticipantStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.accent_yellow))
                    binding.layoutActionButtons.visibility = View.VISIBLE

                    binding.btnAccept.setOnClickListener { onAcceptClick(item) }
                    binding.btnReject.setOnClickListener { onRejectClick(item) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemManageParticipantBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
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
