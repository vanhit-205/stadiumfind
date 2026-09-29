package com.example.stadiumfind.core.ui.components

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.stadiumfind.R
import com.example.stadiumfind.databinding.DialogLoadingBinding

/**
 * LoadingDialog — DialogFragment overlay toàn màn hình khi gọi API.
 *
 * Sử dụng:
 *   // Show
 *   loadingDialog.show(parentFragmentManager, "loading")
 *   // Hoặc với message
 *   loadingDialog.show(parentFragmentManager, "loading", "Đang đăng nhập...")
 *
 *   // Dismiss
 *   loadingDialog.safeDismiss()
 */
class LoadingDialog : DialogFragment() {

    private var _binding: DialogLoadingBinding? = null
    private val binding get() = _binding!!

    private var message: String? = null

    companion object {
        private const val ARG_MESSAGE = "message"
        private const val TAG = "LoadingDialog"

        fun newInstance(message: String? = null): LoadingDialog {
            return LoadingDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_MESSAGE, message)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        message = arguments?.getString(ARG_MESSAGE)
        // Không thể dismiss bằng cách bấm ngoài hoặc back
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogLoadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hiện message nếu có
        if (!message.isNullOrEmpty()) {
            binding.tvLoadingMessage.text = message
            binding.tvLoadingMessage.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        // Làm dialog full screen với nền trong suốt
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Safe dismiss — tránh crash khi Fragment đã bị detach
     */
    fun safeDismiss() {
        if (isAdded && !isDetached) {
            dismissAllowingStateLoss()
        }
    }
}

/**
 * Extension để show LoadingDialog dễ hơn.
 *
 * Sử dụng trong Fragment:
 *   private val loadingDialog = LoadingDialog.newInstance()
 *   loadingDialog.show(childFragmentManager)
 *   loadingDialog.safeDismiss()
 */
fun LoadingDialog.show(fragmentManager: FragmentManager, message: String? = null) {
    if (!isAdded) {
        if (message != null) {
            LoadingDialog.newInstance(message).show(fragmentManager, "LoadingDialog")
        } else {
            show(fragmentManager, "LoadingDialog")
        }
    }
}
