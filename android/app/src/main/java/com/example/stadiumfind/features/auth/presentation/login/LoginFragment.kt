package com.example.stadiumfind.features.auth.presentation.login

import android.os.Bundle
import android.text.InputType
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
import com.example.stadiumfind.databinding.FragmentLoginBinding
import com.example.stadiumfind.features.auth.presentation.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by activityViewModels()
    private val loadingDialog by lazy { LoadingDialog.newInstance("Đang đăng nhập...") }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInputType()
        setupListeners()
        observeLoginState()
    }

    private fun setupInputType() {
        binding.tfUsernameOrEmail.setInputType(
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        )
    }

    private fun setupListeners() {
        // Xóa lỗi khi user bắt đầu nhập lại
        binding.tfUsernameOrEmail.getEditText().addTextChangedListener {
            binding.tfUsernameOrEmail.clearError()
        }
        binding.tfPassword.getEditText().addTextChangedListener {
            binding.tfPassword.clearError()
        }

        binding.btnLogin.setOnClickListener {
            if (validate()) {
                viewModel.login(
                    usernameOrEmail = binding.tfUsernameOrEmail.getText(),
                    password = binding.tfPassword.getText()
                )
            }
        }

        binding.tvGoRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun observeLoginState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                            loadingDialog.show(childFragmentManager)
                            binding.btnLogin.setEnabled(false)
                        }
                        is Resource.Success -> {
                            loadingDialog.safeDismiss()
                            binding.btnLogin.setEnabled(true)
                            viewModel.consumeLoginState()
                            // Điều hướng vào Home, xóa Auth stack
                            findNavController().navigate(
                                R.id.action_loginFragment_to_homeFragment,
                                null,
                                androidx.navigation.NavOptions.Builder()
                                    .setPopUpTo(R.id.loginFragment, true)
                                    .build()
                            )
                        }
                        is Resource.Error -> {
                            loadingDialog.safeDismiss()
                            binding.btnLogin.setEnabled(true)
                            viewModel.consumeLoginState()
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

        val usernameOrEmail = binding.tfUsernameOrEmail.getText()
        val password = binding.tfPassword.getText()

        if (usernameOrEmail.isEmpty()) {
            binding.tfUsernameOrEmail.setError("Vui lòng nhập email hoặc tên đăng nhập")
            isValid = false
        }

        if (password.isEmpty()) {
            binding.tfPassword.setError("Vui lòng nhập mật khẩu")
            isValid = false
        } else if (password.length < 6) {
            binding.tfPassword.setError("Mật khẩu phải có ít nhất 6 ký tự")
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
