package com.example.stadiumfind.features.stadium.presentation.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.stadiumfind.R
import com.example.stadiumfind.databinding.ItemStadiumImageSliderBinding

/**
 * Adapter cho ViewPager2 hiển thị danh sách ảnh sân bóng dạng slider.
 */
class StadiumImageSliderAdapter(
    private val images: List<String>
) : RecyclerView.Adapter<StadiumImageSliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(
        private val binding: ItemStadiumImageSliderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUrl: String) {
            binding.ivSliderImage.load(imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_stadium_placeholder)
                error(R.drawable.ic_stadium_placeholder)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding = ItemStadiumImageSliderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size
}
