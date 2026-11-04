package com.example.stadiumfind.features.stadium.presentation.bottomsheet

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.stadiumfind.core.util.loadStadiumImage
import com.example.stadiumfind.databinding.BottomSheetStadiumPreviewBinding
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Locale

/**
 * BottomSheetDialogFragment hiển thị bản xem trước thông tin sân bóng khi người dùng click vào Marker trên bản đồ.
 */
class StadiumPreviewBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetStadiumPreviewBinding? = null
    private val binding get() = _binding!!

    var onDetailClick: ((Long) -> Unit)? = null

    companion object {
        const val TAG = "StadiumPreviewBottomSheet"

        private const val ARG_ID = "arg_id"
        private const val ARG_NAME = "arg_name"
        private const val ARG_ADDRESS = "arg_address"
        private const val ARG_IMAGE_URL = "arg_image_url"
        private const val ARG_RATING = "arg_rating"
        private const val ARG_PRICE = "arg_price"
        private const val ARG_DISTANCE = "arg_distance"
        private const val ARG_HOURS = "arg_hours"
        private const val ARG_SPORT = "arg_sport"
        private const val ARG_LAT = "arg_lat"
        private const val ARG_LNG = "arg_lng"

        fun newInstance(
            stadium: StadiumItem,
            onDetailClick: ((Long) -> Unit)? = null
        ): StadiumPreviewBottomSheet {
            return StadiumPreviewBottomSheet().apply {
                this.onDetailClick = onDetailClick
                arguments = Bundle().apply {
                    putLong(ARG_ID, stadium.id)
                    putString(ARG_NAME, stadium.name)
                    putString(ARG_ADDRESS, stadium.address)
                    putString(ARG_IMAGE_URL, stadium.imageUrl)
                    putDouble(ARG_RATING, stadium.rating)
                    putString(ARG_PRICE, stadium.formattedPrice)
                    putString(ARG_DISTANCE, stadium.formattedDistance)
                    putString(ARG_HOURS, stadium.formattedOperatingHours)
                    putString(ARG_SPORT, stadium.primarySportBadge)
                    putDouble(ARG_LAT, stadium.latitude)
                    putDouble(ARG_LNG, stadium.longitude)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetStadiumPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments ?: return
        val stadiumId = args.getLong(ARG_ID, 0L)
        val name = args.getString(ARG_NAME).orEmpty()
        val address = args.getString(ARG_ADDRESS).orEmpty()
        val imageUrl = args.getString(ARG_IMAGE_URL)
        val rating = args.getDouble(ARG_RATING, 5.0)
        val formattedPrice = args.getString(ARG_PRICE).orEmpty()
        val distanceStr = args.getString(ARG_DISTANCE).orEmpty()
        val hours = args.getString(ARG_HOURS).orEmpty()
        val sport = args.getString(ARG_SPORT).orEmpty()
        val latitude = args.getDouble(ARG_LAT, 0.0)
        val longitude = args.getDouble(ARG_LNG, 0.0)

        binding.apply {
            tvStadiumName.text = name
            tvAddress.text = address
            tvOperatingHours.text = hours
            tvSportBadge.text = sport.ifBlank { "Bóng đá" }
            tvRating.text = String.format(Locale.US, "%.1f", rating)
            tvPrice.text = formattedPrice

            if (distanceStr.isNotBlank()) {
                tvDistance.visibility = View.VISIBLE
                tvDistance.text = "📍 Cách bạn $distanceStr"
            } else {
                tvDistance.visibility = View.GONE
            }

            ivStadiumPreview.loadStadiumImage(imageUrl)

            // Sự kiện nút Chỉ đường (Directions)
            btnDirections.setOnClickListener {
                openDirectionsIntent(latitude, longitude, name)
            }

            // Sự kiện nút Xem chi tiết & Đặt sân
            btnViewDetail.setOnClickListener {
                dismiss()
                onDetailClick?.invoke(stadiumId)
            }
        }
    }

    /**
     * Mở ứng dụng chỉ đường ngoài (Google Maps hoặc Browser) tới tọa độ sân bóng.
     */
    private fun openDirectionsIntent(lat: Double, lng: Double, stadiumName: String) {
        if (lat == 0.0 && lng == 0.0) {
            Toast.makeText(requireContext(), "Tọa độ sân chưa được thiết lập", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Intent mở chỉ đường trong Google Maps
            val gmmIntentUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(${Uri.encode(stadiumName)})")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                setPackage("com.google.android.apps.maps")
            }
            startActivity(mapIntent)
        } catch (_: Exception) {
            try {
                // Fallback: Mở Google Maps qua trình duyệt
                val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                startActivity(webIntent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Không thể mở ứng dụng bản đồ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
