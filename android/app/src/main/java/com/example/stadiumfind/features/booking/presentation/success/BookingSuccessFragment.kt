package com.example.stadiumfind.features.booking.presentation.success

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.stadiumfind.R
import com.example.stadiumfind.core.util.QrCodeGenerator
import com.example.stadiumfind.databinding.FragmentBookingSuccessBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Màn hình Vé Điện Tử & Mã QR Xác Nhận (E-Ticket / Booking Success Screen).
 * Hiển thị thẻ vé hoàn chỉnh, mã QR sắc nét, hỗ trợ copy mã vé, chia sẻ và về trang chủ an toàn.
 */
@AndroidEntryPoint
class BookingSuccessFragment : Fragment() {

    private var _binding: FragmentBookingSuccessBinding? = null
    private val binding get() = _binding!!

    private var ticketId: Long = 0L
    private var stadiumName: String = ""
    private var courtName: String = ""
    private var courtId: Long = 0L
    private var stadiumAddress: String = ""
    private var bookingDate: String = ""
    private var timeRange: String = ""
    private var totalPrice: String = ""
    private var note: String? = null

    companion object {
        const val ARG_TICKET_ID = "ticketId"
        const val ARG_STADIUM_NAME = "stadiumName"
        const val ARG_COURT_NAME = "courtName"
        const val ARG_COURT_ID = "courtId"
        const val ARG_STADIUM_ADDRESS = "stadiumAddress"
        const val ARG_BOOKING_DATE = "bookingDate"
        const val ARG_TIME_RANGE = "timeRange"
        const val ARG_TOTAL_PRICE = "totalPrice"
        const val ARG_NOTE = "note"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            ticketId = it.getLong(ARG_TICKET_ID, 0L)
            stadiumName = it.getString(ARG_STADIUM_NAME) ?: "Sân bóng đá"
            courtName = it.getString(ARG_COURT_NAME) ?: "Sân tiêu chuẩn"
            courtId = it.getLong(ARG_COURT_ID, 0L)
            stadiumAddress = it.getString(ARG_STADIUM_ADDRESS) ?: "Vui lòng xem chi tiết trên bản đồ"
            bookingDate = it.getString(ARG_BOOKING_DATE) ?: ""
            timeRange = it.getString(ARG_TIME_RANGE) ?: ""
            totalPrice = it.getString(ARG_TOTAL_PRICE) ?: "0đ"
            note = it.getString(ARG_NOTE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackPressHandler()
        bindTicketData()
        generateAndDisplayQrCode()
        setupListeners()
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateToHome()
                }
            }
        )
    }

    private fun bindTicketData() {
        binding.tvTicketStadiumName.text = stadiumName
        binding.tvTicketCourtName.text = courtName
        binding.tvTicketAddress.text = stadiumAddress
        binding.tvTicketCode.text = "Mã vé: #SF-$ticketId"
        binding.tvTicketDate.text = bookingDate
        binding.tvTicketTime.text = timeRange
        binding.tvTicketPrice.text = totalPrice

        if (!note.isNullOrBlank()) {
            binding.tvTicketNote.visibility = View.VISIBLE
            binding.tvTicketNote.text = "Ghi chú: $note"
        } else {
            binding.tvTicketNote.visibility = View.GONE
        }
    }

    private fun generateAndDisplayQrCode() {
        val payload = QrCodeGenerator.createTicketPayload(
            ticketId = ticketId,
            courtId = courtId,
            stadiumName = stadiumName,
            courtName = courtName,
            bookingDate = bookingDate,
            timeRange = timeRange
        )

        binding.pbQrLoading.visibility = View.VISIBLE
        binding.ivQrCode.visibility = View.INVISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            val qrBitmap = QrCodeGenerator.generateQrBitmap(
                content = payload,
                size = 512
            )

            binding.pbQrLoading.visibility = View.GONE
            if (qrBitmap != null) {
                binding.ivQrCode.setImageBitmap(qrBitmap)
                binding.ivQrCode.alpha = 0f
                binding.ivQrCode.visibility = View.VISIBLE
                binding.ivQrCode.animate().alpha(1f).setDuration(250).start()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Không thể tạo mã QR, vui lòng lưu mã vé để đối soát",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupListeners() {
        // Sao chép mã vé vào Clipboard
        binding.layoutCopyTicket.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Ticket Code", "SF-$ticketId")
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Đã sao chép mã vé #SF-$ticketId", Toast.LENGTH_SHORT).show()
        }

        // Chia sẻ thông tin vé qua các ứng dụng khác (Zalo, Messenger, SMS...)
        binding.btnShareTicket.setOnClickListener {
            shareTicketInfo()
        }

        // Về trang chủ, làm sạch back stack
        binding.btnHome.setOnClickListener {
            navigateToHome()
        }
    }

    private fun shareTicketInfo() {
        val shareText = """
            ⚽ VÉ ĐẶT SÂN BÓNG - STADIUMFIND
            ════════════════════════════════
            • Mã vé: #SF-$ticketId
            • Cụm sân: $stadiumName
            • Sân: $courtName
            • Địa chỉ: $stadiumAddress
            • Ngày đá: $bookingDate
            • Khung giờ: $timeRange
            • Tổng tiền: $totalPrice
            ════════════════════════════════
            Xuất trình mã vé hoặc mã QR trên ứng dụng StadiumFind tại quầy để nhận sân!
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Chia sẻ vé đặt sân")
        startActivity(shareIntent)
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_bookingSuccessFragment_to_homeFragment)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
