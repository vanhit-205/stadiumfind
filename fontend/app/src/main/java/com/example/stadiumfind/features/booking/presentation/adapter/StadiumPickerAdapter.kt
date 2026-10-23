package com.example.stadiumfind.features.booking.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stadiumfind.R
import com.example.stadiumfind.databinding.ItemSelectStadiumBinding
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem

/**
 * Adapter hiển thị danh sách Cụm Sân trong BottomSheet chọn sân.
 */
class StadiumPickerAdapter(
    private val onStadiumClick: (StadiumItem) -> Unit
) : ListAdapter<StadiumItem, StadiumPickerAdapter.StadiumViewHolder>(DiffCallback) {

    private var selectedStadiumId: Long? = null

    fun setSelectedId(id: Long?) {
        selectedStadiumId = id
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StadiumViewHolder {
        val binding = ItemSelectStadiumBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StadiumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StadiumViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StadiumViewHolder(
        private val binding: ItemSelectStadiumBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(stadium: StadiumItem) {
            val context = itemView.context
            val isSelected = (stadium.id == selectedStadiumId)

            binding.tvStadiumName.text = stadium.name
            binding.tvStadiumAddress.text = stadium.address
            binding.tvRating.text = "⭐ ${String.format(java.util.Locale.US, "%.1f", stadium.rating)}"
            binding.tvTotalCourts.text = "• ${stadium.totalCourts} sân con"
            binding.tvPrice.text = stadium.formattedPrice

            if (isSelected) {
                binding.cardStadiumItem.strokeColor = ContextCompat.getColor(context, R.color.primary_300)
                binding.ivCheckSelected.visibility = View.VISIBLE
            } else {
                binding.cardStadiumItem.strokeColor = ContextCompat.getColor(context, R.color.stroke_default)
                binding.ivCheckSelected.visibility = View.GONE
            }

            binding.cardStadiumItem.setOnClickListener {
                onStadiumClick(stadium)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<StadiumItem>() {
        override fun areItemsTheSame(oldItem: StadiumItem, newItem: StadiumItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StadiumItem, newItem: StadiumItem): Boolean {
            return oldItem == newItem
        }
    }
}
