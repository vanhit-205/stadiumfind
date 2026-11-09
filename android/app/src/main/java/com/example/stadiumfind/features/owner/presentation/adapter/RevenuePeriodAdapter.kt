package com.example.stadiumfind.features.owner.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stadiumfind.databinding.ItemRevenuePeriodBinding
import com.example.stadiumfind.features.owner.domain.model.RevenueItem
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class RevenuePeriodAdapter :
    ListAdapter<RevenueItem, RevenuePeriodAdapter.RevenuePeriodViewHolder>(DIFF_CALLBACK) {

    private val formatter: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale("vi", "VN")).apply {
            groupingSeparator = ','
        }
        DecimalFormat("#,###", symbols)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RevenuePeriodViewHolder {
        val binding = ItemRevenuePeriodBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RevenuePeriodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RevenuePeriodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RevenuePeriodViewHolder(
        private val binding: ItemRevenuePeriodBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RevenueItem) {
            binding.tvPeriod.text = item.period
            binding.tvBookingCount.text = "${item.bookingCount} lượt đặt sân"
            val formattedAmount = formatter.format(item.revenue) + " đ"
            binding.tvRevenueAmount.text = formattedAmount
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RevenueItem>() {
            override fun areItemsTheSame(oldItem: RevenueItem, newItem: RevenueItem): Boolean =
                oldItem.period == newItem.period

            override fun areContentsTheSame(oldItem: RevenueItem, newItem: RevenueItem): Boolean =
                oldItem == newItem
        }
    }
}
