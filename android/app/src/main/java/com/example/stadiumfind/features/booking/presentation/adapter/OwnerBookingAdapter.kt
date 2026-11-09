package com.example.stadiumfind.features.booking.presentation.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stadiumfind.R
import com.example.stadiumfind.features.booking.domain.model.BookingResult
import com.google.android.material.button.MaterialButton

class OwnerBookingAdapter(
    private val onApproveClick: (BookingResult) -> Unit,
    private val onRejectClick: (BookingResult) -> Unit
) : ListAdapter<BookingResult, OwnerBookingAdapter.OwnerBookingViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerBookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_owner_booking, parent, false)
        return OwnerBookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OwnerBookingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OwnerBookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStadiumName: TextView = itemView.findViewById(R.id.tvOwnerStadiumName)
        private val tvStatusBadge: TextView = itemView.findViewById(R.id.tvOwnerStatusBadge)
        private val tvCourtAndCustomer: TextView = itemView.findViewById(R.id.tvOwnerCourtAndCustomer)
        private val tvBookingDate: TextView = itemView.findViewById(R.id.tvOwnerBookingDate)
        private val tvBookingTime: TextView = itemView.findViewById(R.id.tvOwnerBookingTime)
        private val tvTotalPrice: TextView = itemView.findViewById(R.id.tvOwnerTotalPrice)
        private val tvNote: TextView = itemView.findViewById(R.id.tvOwnerNote)
        private val layoutOwnerActions: View = itemView.findViewById(R.id.layoutOwnerActions)
        private val dividerOwnerActions: View = itemView.findViewById(R.id.dividerOwnerActions)
        private val btnApprove: MaterialButton = itemView.findViewById(R.id.btnOwnerApprove)
        private val btnReject: MaterialButton = itemView.findViewById(R.id.btnOwnerReject)

        fun bind(booking: BookingResult) {
            tvStadiumName.text = booking.stadiumName
            val customerName = booking.userName ?: "Khách hàng"
            tvCourtAndCustomer.text = "${booking.courtName} • Khách: $customerName"
            tvBookingDate.text = "📅 ${booking.bookingDate}"
            tvBookingTime.text = "⏰ ${booking.displayTimeRange}"
            tvTotalPrice.text = "Tổng: ${booking.formattedTotalPrice}"

            if (!booking.note.isNullOrBlank()) {
                tvNote.visibility = View.VISIBLE
                tvNote.text = "Ghi chú: ${booking.note}"
            } else {
                tvNote.visibility = View.GONE
            }

            val statusUpper = booking.status.uppercase()
            when (statusUpper) {
                "PENDING" -> {
                    tvStatusBadge.text = "Chờ Duyệt"
                    tvStatusBadge.setTextColor(Color.parseColor("#FFB300"))
                    setBadgeBackground(tvStatusBadge, "#1AFFB300", "#FFB300")
                    layoutOwnerActions.visibility = View.VISIBLE
                    dividerOwnerActions.visibility = View.VISIBLE
                }
                "CONFIRMED" -> {
                    tvStatusBadge.text = "Đã Duyệt"
                    tvStatusBadge.setTextColor(Color.parseColor("#66BB6A"))
                    setBadgeBackground(tvStatusBadge, "#1A2E7D32", "#43A047")
                    layoutOwnerActions.visibility = View.GONE
                    dividerOwnerActions.visibility = View.GONE
                }
                "CANCELLED" -> {
                    tvStatusBadge.text = "Đã Từ Chối"
                    tvStatusBadge.setTextColor(Color.parseColor("#EF5350"))
                    setBadgeBackground(tvStatusBadge, "#1AEF5350", "#EF5350")
                    layoutOwnerActions.visibility = View.GONE
                    dividerOwnerActions.visibility = View.GONE
                }
                else -> {
                    tvStatusBadge.text = booking.status
                    tvStatusBadge.setTextColor(Color.parseColor("#AAAAAA"))
                    setBadgeBackground(tvStatusBadge, "#1A424242", "#757575")
                    layoutOwnerActions.visibility = View.GONE
                    dividerOwnerActions.visibility = View.GONE
                }
            }

            btnApprove.setOnClickListener { onApproveClick(booking) }
            btnReject.setOnClickListener { onRejectClick(booking) }
        }

        private fun setBadgeBackground(view: TextView, solidColorHex: String, strokeColorHex: String) {
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 16f
                setColor(Color.parseColor(solidColorHex))
                setStroke(2, Color.parseColor(strokeColorHex))
            }
            view.background = drawable
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BookingResult>() {
            override fun areItemsTheSame(oldItem: BookingResult, newItem: BookingResult): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: BookingResult, newItem: BookingResult): Boolean {
                return oldItem == newItem
            }
        }
    }
}
