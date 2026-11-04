package com.example.stadiumfind.features.booking.presentation.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.stadiumfind.R
import com.example.stadiumfind.features.booking.domain.model.BookingResult
import com.google.android.material.button.MaterialButton

class BookingTicketAdapter(
    private val onTicketClick: (BookingResult) -> Unit,
    private val onQrClick: (BookingResult) -> Unit
) : ListAdapter<BookingResult, BookingTicketAdapter.TicketViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking_ticket, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivStadiumThumb: ImageView = itemView.findViewById(R.id.ivStadiumThumb)
        private val tvStadiumName: TextView = itemView.findViewById(R.id.tvStadiumName)
        private val tvCourtName: TextView = itemView.findViewById(R.id.tvCourtName)
        private val tvStatusBadge: TextView = itemView.findViewById(R.id.tvStatusBadge)
        private val tvBookingDate: TextView = itemView.findViewById(R.id.tvBookingDate)
        private val tvBookingTime: TextView = itemView.findViewById(R.id.tvBookingTime)
        private val tvTotalPrice: TextView = itemView.findViewById(R.id.tvTotalPrice)
        private val btnViewQr: MaterialButton = itemView.findViewById(R.id.btnViewQr)

        fun bind(ticket: BookingResult) {
            tvStadiumName.text = ticket.stadiumName
            tvCourtName.text = ticket.courtName
            tvBookingDate.text = ticket.bookingDate
            tvBookingTime.text = ticket.displayTimeRange
            tvTotalPrice.text = ticket.formattedTotalPrice

            if (!ticket.stadiumImageUrl.isNullOrBlank()) {
                ivStadiumThumb.load(ticket.stadiumImageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_calendar)
                    error(R.drawable.ic_calendar)
                }
            } else {
                ivStadiumThumb.setImageResource(R.drawable.ic_calendar)
            }

            // Định dạng Badge trạng thái
            when (ticket.status.uppercase()) {
                "CONFIRMED", "PENDING" -> {
                    tvStatusBadge.text = if (ticket.status.equals("CONFIRMED", true)) "Đã Xác Nhận" else "Chờ Xác Nhận"
                    tvStatusBadge.setTextColor(Color.parseColor("#66BB6A"))
                    setBadgeBackground(tvStatusBadge, "#1A2E7D32", "#43A047")
                    btnViewQr.visibility = View.VISIBLE
                }
                "COMPLETED" -> {
                    tvStatusBadge.text = "Đã Hoàn Thành"
                    tvStatusBadge.setTextColor(Color.parseColor("#AAAAAA"))
                    setBadgeBackground(tvStatusBadge, "#1A424242", "#757575")
                    btnViewQr.visibility = View.GONE
                }
                "CANCELLED" -> {
                    tvStatusBadge.text = "Đã Hủy"
                    tvStatusBadge.setTextColor(Color.parseColor("#EF5350"))
                    setBadgeBackground(tvStatusBadge, "#1AEF5350", "#EF5350")
                    btnViewQr.visibility = View.GONE
                }
                else -> {
                    tvStatusBadge.text = ticket.status
                    tvStatusBadge.setTextColor(Color.parseColor("#66BB6A"))
                    setBadgeBackground(tvStatusBadge, "#1A2E7D32", "#43A047")
                    btnViewQr.visibility = View.VISIBLE
                }
            }

            btnViewQr.setOnClickListener { onQrClick(ticket) }
            itemView.setOnClickListener { onTicketClick(ticket) }
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
