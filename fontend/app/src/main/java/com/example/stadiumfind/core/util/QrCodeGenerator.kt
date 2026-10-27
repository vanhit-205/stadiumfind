package com.example.stadiumfind.core.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.EnumMap

/**
 * Tiện ích tạo Bitmap mã QR Code sử dụng ZXing Core.
 * Hoạt động bất đồng bộ trên [Dispatchers.Default] để đảm bảo không làm giật lag UI Thread.
 */
object QrCodeGenerator {

    /**
     * Sinh đối tượng [Bitmap] từ chuỗi [content] (JSON, URL hoặc Ticket ID).
     *
     * @param content Nội dung cần mã hóa thành QR Code.
     * @param size Chiều rộng và chiều cao của ảnh QR (mặc định 512px).
     * @param qrColor Màu của các điểm pixel QR (mặc định đen).
     * @param backgroundColor Màu nền của ảnh QR (mặc định trắng).
     * @return [Bitmap] ảnh QR Code sắc nét, hoặc `null` nếu xảy ra lỗi.
     */
    suspend fun generateQrBitmap(
        content: String,
        size: Int = 512,
        qrColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE
    ): Bitmap? = withContext(Dispatchers.Default) {
        if (content.isBlank() || size <= 0) return@withContext null

        try {
            val hints: MutableMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
            // Hỗ trợ tiếng Việt có dấu
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            // Mức sửa lỗi cao nhất 30% giúp quét tốt kể cả khi màn hình tối hoặc bám bụi
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
            // Margin = 1 để viền trắng tối thiểu, mã QR to rõ nhất trong khung ảnh
            hints[EncodeHintType.MARGIN] = 1

            val bitMatrix = MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                size,
                size,
                hints
            )

            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)

            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) qrColor else backgroundColor
                }
            }

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Tạo chuỗi payload định dạng JSON chuẩn cho vé đặt sân.
     */
    fun createTicketPayload(
        ticketId: Long,
        courtId: Long,
        stadiumName: String,
        courtName: String,
        bookingDate: String,
        timeRange: String
    ): String {
        return """{"app":"StadiumFind","ticketId":$ticketId,"stadium":"$stadiumName","court":"$courtName","courtId":$courtId,"date":"$bookingDate","time":"$timeRange"}"""
    }
}
