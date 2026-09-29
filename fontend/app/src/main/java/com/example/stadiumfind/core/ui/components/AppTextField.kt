package com.example.stadiumfind.core.ui.components

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.stadiumfind.R
import com.example.stadiumfind.databinding.ViewAppTextFieldBinding

/**
 * AppTextField — Custom TextInput với label, icon, error và password toggle.
 *
 * Sử dụng:
 *   binding.tfEmail.hint = "Nhập email"
 *   binding.tfEmail.setError("Email không hợp lệ")
 *   binding.tfEmail.getText()
 *   binding.tfEmail.clear()
 */
class AppTextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = ViewAppTextFieldBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    var hint: String
        get() = binding.textInputLayout.hint?.toString() ?: ""
        set(value) { binding.textInputLayout.hint = value }

    init {
        orientation = VERTICAL

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AppTextField,
            defStyleAttr,
            0
        ).apply {
            try {
                // Label
                getString(R.styleable.AppTextField_fieldLabel)?.let { label ->
                    binding.tvLabel.text = label
                    binding.tvLabel.visibility = VISIBLE
                }

                // Hint
                getString(R.styleable.AppTextField_android_hint)?.let {
                    hint = it
                }

                // Leading Icon
                getDrawable(R.styleable.AppTextField_leadingIcon)?.let {
                    setLeadingIcon(it)
                }

                // Trailing Icon
                getDrawable(R.styleable.AppTextField_trailingIcon)?.let {
                    setTrailingIcon(it)
                }

                // Input Type
                val inputType = getInt(R.styleable.AppTextField_android_inputType, InputType.TYPE_CLASS_TEXT)
                binding.editText.inputType = inputType

                // Password toggle
                if (getBoolean(R.styleable.AppTextField_isPassword, false)) {
                    enablePasswordToggle()
                }

            } finally {
                recycle()
            }
        }
    }

    /** Trả về text đã nhập, trim whitespace */
    fun getText(): String = binding.editText.text?.toString()?.trim() ?: ""

    /** Xóa nội dung */
    fun clear() {
        binding.editText.setText("")
        clearError()
    }

    /** Hiện error message bên dưới field */
    fun setError(message: String?) {
        binding.textInputLayout.error = message
        binding.textInputLayout.isErrorEnabled = !message.isNullOrEmpty()
    }

    /** Xóa error */
    fun clearError() {
        binding.textInputLayout.error = null
        binding.textInputLayout.isErrorEnabled = false
    }

    /** Set leading icon (start) */
    fun setLeadingIcon(icon: Drawable?) {
        binding.textInputLayout.startIconDrawable = icon
        binding.textInputLayout.setStartIconTintList(
            context.getColorStateList(R.color.text_field_stroke_color)
        )
    }

    /** Set trailing icon (end) */
    fun setTrailingIcon(icon: Drawable?) {
        binding.textInputLayout.endIconDrawable = icon
        binding.textInputLayout.endIconMode =
            com.google.android.material.textfield.TextInputLayout.END_ICON_CUSTOM
    }

    /** Enable password visibility toggle */
    fun enablePasswordToggle() {
        binding.textInputLayout.endIconMode =
            com.google.android.material.textfield.TextInputLayout.END_ICON_PASSWORD_TOGGLE
        binding.editText.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    }

    /** Lấy EditText thô để set TextWatcher hoặc listener */
    fun getEditText() = binding.editText

    /** Set input type (vd: TYPE_CLASS_NUMBER, TYPE_TEXT_VARIATION_EMAIL_ADDRESS) */
    fun setInputType(inputType: Int) {
        binding.editText.inputType = inputType
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        binding.textInputLayout.isEnabled = enabled
        binding.editText.isEnabled = enabled
    }
}
