package com.example.stadiumfind.core.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.stadiumfind.R
import com.example.stadiumfind.databinding.ViewPrimaryButtonBinding

/**
 * PrimaryButton — Custom view có 2 trạng thái:
 *  - Normal: Hiện text + enabled
 *  - Loading: Hiện CircularProgressIndicator, ẩn text + disabled
 *
 * Sử dụng:
 *   binding.btnLogin.text = "Đăng Nhập"
 *   binding.btnLogin.setLoading(true)  // khi gọi API
 *   binding.btnLogin.setLoading(false) // sau khi nhận response
 */
class PrimaryButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewPrimaryButtonBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    var text: String
        get() = binding.btnContent.text.toString()
        set(value) { binding.btnContent.text = value }

    var isLoading: Boolean = false
        private set

    init {
        // Đọc custom attribute từ XML nếu có
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PrimaryButton,
            defStyleAttr,
            0
        ).apply {
            try {
                text = getString(R.styleable.PrimaryButton_android_text) ?: ""
                if (getBoolean(R.styleable.PrimaryButton_isLoading, false)) {
                    setLoading(true)
                }
            } finally {
                recycle()
            }
        }

        // Forward click to inner button
        binding.btnContent.setOnClickListener(null)
    }

    /**
     * Chuyển đổi trạng thái loading.
     * @param loading true = hiện spinner, ẩn text, disable tương tác
     */
    fun setLoading(loading: Boolean) {
        isLoading = loading
        if (loading) {
            binding.btnContent.visibility = INVISIBLE
            binding.loadingContainer.visibility = VISIBLE
            isClickable = false
            isFocusable = false
        } else {
            binding.btnContent.visibility = VISIBLE
            binding.loadingContainer.visibility = GONE
            isClickable = true
            isFocusable = true
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        binding.btnContent.setOnClickListener(l)
    }

    fun setEnabled(enabled: Boolean, disabledAlpha: Float = 0.5f) {
        binding.btnContent.isEnabled = enabled
        binding.btnContent.alpha = if (enabled) 1f else disabledAlpha
        isClickable = enabled
    }
}
