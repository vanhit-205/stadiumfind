package com.example.stadiumfind.features.auth.presentation.register

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.stadiumfind.R
import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.core.ui.components.LoadingDialog
import com.example.stadiumfind.core.ui.components.show
import com.example.stadiumfind.databinding.FragmentRegisterBinding
import com.example.stadiumfind.features.auth.presentation.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by activityViewModels()
    private val loadingDialog by lazy { LoadingDialog.newInstance("Đang tạo tài khoản...") }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeRegisterState()
    }

    private fun setupListeners() {
        // Xóa lỗi khi user bắt đầu nhập
        binding.tfUsername.getEditText().addTextChangedListener { binding.tfUsername.clearError() }
        binding.tfEmail.getEditText().addTextChangedListener { binding.tfEmail.clearError() }
        binding.tfPassword.getEditText().addTextChangedListener { binding.tfPassword.clearError() }
        binding.tfConfirmPassword.getEditText().addTextChangedListener { binding.tfConfirmPassword.clearError() }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnRegister.setOnClickListener {
            if (validate()) {
                viewModel.register(
                    username = binding.tfUsername.getText(),
                    email = binding.tfEmail.getText(),
                    password = binding.tfPassword.getText()
                )
            }
        }

        binding.tvGoLogin.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeRegisterState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerState.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                            loadingDialog.show(childFragmentManager)
                            binding.btnRegister.setEnabled(false)
                        }
                        is Resource.Success -> {
                            loadingDialog.safeDismiss()
                            binding.btnRegister.setEnabled(true)
                            viewModel.consumeRegisterState()
                            // Đăng ký thành công → điều hướng vào Home, xóa toàn bộ Auth stack
                            findNavController().navigate(
                                R.id.action_registerFragment_to_homeFragment,
                                null,
                                androidx.navigation.NavOptions.Builder()
                                    .setPopUpTo(R.id.loginFragment, true)
                                    .build()
                            )
                        }
                        is Resource.Error -> {
                            loadingDialog.safeDismiss()
                            binding.btnRegister.setEnabled(true)
                            viewModel.consumeRegisterState()
                            showSnackbar(state.message)
                        }
                        null -> { /* initial / consumed */ }
                    }
                }
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true

        val username = binding.tfUsername.getText()
        val email = binding.tfEmail.getText()
        val password = binding.tfPassword.getText()
        val confirmPassword = binding.tfConfirmPassword.getText()

        if (username.isEmpty()) {
            binding.tfUsername.setError("Vui lòng nhập tên đăng nhập")
            isValid = false
        } else if (username.length < 3) {
            binding.tfUsername.setError("Tên đăng nhập phải có ít nhất 3 ký tự")
            isValid = false
        }

        if (email.isEmpty()) {
            binding.tfEmail.setError("Vui lòng nhập email")
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tfEmail.setError("Email không hợp lệ")
            isValid = false
        }

        if (password.isEmpty()) {
            binding.tfPassword.setError("Vui lòng nhập mật khẩu")
            isValid = false
        } else if (password.length < 6) {
            binding.tfPassword.setError("Mật khẩu phải có ít nhất 6 ký tự")
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            binding.tfConfirmPassword.setError("Vui lòng xác nhận mật khẩu")
            isValid = false
        } else if (password != confirmPassword) {
            binding.tfConfirmPassword.setError("Mật khẩu xác nhận không khớp")
            isValid = false
        }

        return isValid
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
