package com.example.kalavidarabalaga.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalavidarabalaga.data.repository.AuthRepository
import com.example.kalavidarabalaga.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkCurrentUser()
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(email, pass)
            if (result.isSuccess) {
                checkCurrentUser()
            } else {
                val errorMsg = result.exceptionOrNull()?.localizedMessage ?: "Login Failed"
                _authState.value = AuthState.Error(errorMsg)
            }
        }
    }

    fun register(email: String, pass: String, role: com.example.kalavidarabalaga.domain.model.Role) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.register(email, pass, role)
            if (result.isSuccess) {
                checkCurrentUser()
            } else {
                val errorMsg = result.exceptionOrNull()?.localizedMessage ?: "Registration Failed"
                _authState.value = AuthState.Error(errorMsg)
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _authState.value = AuthState.Unauthenticated
    }

    private fun checkCurrentUser() {
        val user = authRepository.currentUser
        if (user != null) {
            viewModelScope.launch {
                try {
                    val userDetails = authRepository.getUserDetails(user.uid)
                    if (userDetails != null) {
                        _authState.value = AuthState.Authenticated(userDetails)
                    } else {
                        _authState.value = AuthState.Error("User details not found in database.")
                    }
                } catch (e: Exception) {
                    _authState.value = AuthState.Error(e.localizedMessage ?: "Failed to load user")
                }
            }
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }
}
