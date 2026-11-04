package com.example.stadiumfind.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stadiumfind.core.data.UserPreferencesDataStore
import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.auth.domain.model.AuthResult
import com.example.stadiumfind.features.auth.domain.usecase.LoginUseCase
import com.example.stadiumfind.features.auth.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val dataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<AuthResult>?>(null)
    val loginState: StateFlow<Resource<AuthResult>?> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<Resource<AuthResult>?>(null)
    val registerState: StateFlow<Resource<AuthResult>?> = _registerState.asStateFlow()

    fun login(usernameOrEmail: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading
            val result = loginUseCase(usernameOrEmail, password)
            if (result is Resource.Success) {
                persistAuthData(result.data)
            }
            _loginState.value = result
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = Resource.Loading
            val result = registerUseCase(username, email, password)
            if (result is Resource.Success) {
                persistAuthData(result.data)
            }
            _registerState.value = result
        }
    }

    /** Xóa state sau khi Fragment đã xử lý (tránh re-trigger khi rotate) */
    fun consumeLoginState() { _loginState.value = null }
    fun consumeRegisterState() { _registerState.value = null }

    private suspend fun persistAuthData(authResult: AuthResult) {
        dataStore.saveAuthData(
            accessToken = authResult.accessToken,
            refreshToken = authResult.refreshToken,
            userId = authResult.user?.id,
            username = authResult.user?.username,
            email = authResult.user?.email,
            avatarUrl = authResult.user?.avatarUrl
        )
    }
}
