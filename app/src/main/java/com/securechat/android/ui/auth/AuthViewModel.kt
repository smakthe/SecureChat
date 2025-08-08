package com.securechat.android.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.securechat.android.data.model.User
import com.securechat.android.data.repository.UserRepository
import com.securechat.android.security.TwoFactorAuthManager
import kotlinx.coroutines.launch
import java.util.*

class AuthViewModel(
    private val userRepository: UserRepository,
    private val twoFactorAuthManager: TwoFactorAuthManager
) : ViewModel() {
    
    private val _authResult = MutableLiveData<AuthResult>()
    val authResult: LiveData<AuthResult> = _authResult
    
    fun registerUser(phoneNumber: String) {
        viewModelScope.launch {
            try {
                // Generate random handle and avatar
                val randomHandle = "user${System.currentTimeMillis().toString().takeLast(6)}"
                val randomAvatar = (1..5).random()
                
                val user = User(
                    id = UUID.randomUUID().toString(),
                    phoneNumber = phoneNumber,
                    handle = randomHandle,
                    avatarId = randomAvatar,
                    publicKey = "temp_key",
                    isCurrentUser = true
                )
                
                userRepository.saveUser(user)
                _authResult.value = AuthResult.Success(user)
            } catch (e: Exception) {
                _authResult.value = AuthResult.Error("Registration failed")
            }
        }
    }
    
    sealed class AuthResult {
        data class Success(val user: User) : AuthResult()
        data class Error(val message: String) : AuthResult()
    }
}