package com.example.stadiumfind.features.match.presentation.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.stadiumfind.R
import com.example.stadiumfind.databinding.ItemMatchPostBinding
import com.example.stadiumfind.features.match.domain.model.MatchPost
import java.text.DecimalFormat

class MatchFeedAdapter(
    private val onJoinClick: (MatchPost) -> Unit,
    private val onManageClick: (MatchPost) -> Unit,
    private val onItemClick: (MatchPost) -> Unit
) : ListAdapter<MatchPost, MatchFeedAdapter.MatchPostViewHolder>(DiffCallback) {

    private val currencyFormatter = DecimalFormat("#,###")
    private var currentUserId: Long? = null
    private var userParticipationMap: Map<Long, String> = emptyMap()

    fun setCurrentUserId(userId: Long?) {
        if (this.currentUserId != userId) {
            this.currentUserId = userId
            notifyDataSetChanged()
        }
    }

    fun setUserParticipationMap(map: Map<Long, String>) {
        if (this.userParticipationMap != map) {
            this.userParticipationMap = map
            notifyDataSetChanged()
        }
    }

    inner class MatchPostViewHolder(
        private val binding: ItemMatchPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MatchPost) {
            val context = binding.root.context
            val isCreator = (currentUserId != null && item.creatorId == currentUserId)
            val participationStatus = userParticipationMap[item.id]?.uppercase()

            // Creator info
            binding.tvCreatorName.text = if (isCreator) "${item.creatorName} (Bạn)" else item.creatorName
            binding.ivCreatorAvatar.load(item.creatorAvatar) {
                crossfade(true)
                placeholder(R.drawable.ic_person)
                error(R.drawable.ic_person)
                transformations(CircleCropTransformation())
            }

            // Sport badge & icon
            val sportIcon = when (item.sportType.uppercase()) {
                "FOOTBALL" -> "⚽ Bóng đá"
                "BADMINTON" -> "🏸 Cầu lông"
                "PICKLEBALL", "TENNIS" -> "🎾 Pickleball/Tennis"
                else -> "🏆 ${item.sportType}"
            }
            binding.tvSportType.text = sportIcon

            // Status badge & Action Button (5 trạng thái rõ ràng)
            when {
                isCreator -> {
                    // 1. Kèo của chính người dùng
                    binding.tvStatusBadge.text = "Kèo của bạn"
                    binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_open)
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.primary_300))

                    binding.btnJoinMatch.isEnabled = true
                    binding.btnJoinMatch.alpha = 1.0f
                    binding.btnJoinMatch.text = "Quản Lý Kèo"
                    binding.btnJoinMatch.setBackgroundResource(R.drawable.bg_badge_open)
                    binding.btnJoinMatch.setTextColor(ContextCompat.getColor(context, R.color.primary_300))
                    binding.btnJoinMatch.setOnClickListener { onManageClick(item) }
                }
                participationStatus == "PENDING" -> {
                    // 2. Người dùng đã gửi yêu cầu, đang chờ chủ bài duyệt
                    binding.tvStatusBadge.text = "Chờ duyệt"
                    binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_pending)
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.warning_amber))

                    binding.btnJoinMatch.isEnabled = false
                    binding.btnJoinMatch.alpha = 0.85f
                    binding.btnJoinMatch.text = "Đang Chờ Duyệt"
                    binding.btnJoinMatch.setBackgroundResource(R.drawable.bg_badge_pending)
                    binding.btnJoinMatch.setTextColor(ContextCompat.getColor(context, R.color.warning_amber))
                    binding.btnJoinMatch.setOnClickListener(null)
                }
                participationStatus == "ACCEPTED" -> {
                    // 3. Người dùng đã được duyệt vào đội
                    binding.tvStatusBadge.text = "Đã trong đội"
                    binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_accepted)
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.success_green))

                    binding.btnJoinMatch.isEnabled = false
                    binding.btnJoinMatch.alpha = 0.9f
                    binding.btnJoinMatch.text = "Đã Tham Gia"
                    binding.btnJoinMatch.setBackgroundResource(R.drawable.bg_badge_accepted)
                    binding.btnJoinMatch.setTextColor(ContextCompat.getColor(context, R.color.success_green))
                    binding.btnJoinMatch.setOnClickListener(null)
                }
                item.isFull -> {
                    // 4. Kèo đã đủ người
                    binding.tvStatusBadge.text = "Đã đủ người"
                    binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_full)
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.error_red))

                    binding.btnJoinMatch.isEnabled = false
                    binding.btnJoinMatch.alpha = 0.5f
                    binding.btnJoinMatch.text = "Đã Đủ"
                    binding.btnJoinMatch.setBackgroundResource(R.drawable.bg_btn_book_sm)
                    binding.btnJoinMatch.setTextColor(ContextCompat.getColor(context, R.color.text_on_primary))
                    binding.btnJoinMatch.setOnClickListener(null)
                }
                else -> {
                    // 5. Kèo còn slot và người dùng chưa tham gia
                    binding.tvStatusBadge.text = "Còn ${item.remainingSlots} slot"
                    binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_open)
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.primary_300))

                    binding.btnJoinMatch.isEnabled = true
                    binding.btnJoinMatch.alpha = 1.0f
                    binding.btnJoinMatch.text = "Tham Gia Kèo"
                    binding.btnJoinMatch.setBackgroundResource(R.drawable.bg_btn_book_sm)
                    binding.btnJoinMatch.setTextColor(ContextCompat.getColor(context, R.color.text_on_primary))
                    binding.btnJoinMatch.setOnClickListener { onJoinClick(item) }
                }
            }

            // Post content
            binding.tvMatchTitle.text = item.title
            binding.tvMatchDescription.text = if (item.description.isNotBlank()) item.description else "Không có mô tả chi tiết."

            // Location
            val courtInfo = if (item.courtName.isNotBlank()) " • ${item.courtName}" else ""
            binding.tvStadiumAndCourt.text = "${item.stadiumName}$courtInfo"

            // Time & Date
            binding.tvDateTimeRange.text = "${item.timeRangeFormatted} • ${item.playDate}"

            // Price
            if (item.pricePerPerson > 0) {
                binding.tvPricePerPerson.text = "${currencyFormatter.format(item.pricePerPerson)} đ / người"
            } else {
                binding.tvPricePerPerson.text = "Miễn phí / Thỏa thuận"
            }

            // Click listeners
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchPostViewHolder {
        val binding = ItemMatchPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchPostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MatchPost>() {
        override fun areItemsTheSame(oldItem: MatchPost, newItem: MatchPost): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MatchPost, newItem: MatchPost): Boolean {
            return oldItem == newItem
        }
    }
}
