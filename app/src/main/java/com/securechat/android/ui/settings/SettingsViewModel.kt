package com.securechat.android.ui.settings

import androidx.lifecycle.*
import com.securechat.android.data.model.User
import com.securechat.android.data.repository.UserRepository
import com.securechat.android.security.TwoFactorAuthManager
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userRepository: UserRepository,
    private val twoFactorAuthManager: TwoFactorAuthManager
) : ViewModel() {

    val currentUser: LiveData<User?> = userRepository.getCurrentUser()

    private val _is2FAEnabled = MutableLiveData<Boolean>()
    val is2FAEnabled: LiveData<Boolean> = _is2FAEnabled

    private val _handleUpdateResult = MutableLiveData<HandleUpdateResult>()
    val handleUpdateResult: LiveData<HandleUpdateResult> = _handleUpdateResult

    private val _panicModeActivated = MutableLiveData<Boolean>()
    val panicModeActivated: LiveData<Boolean> = _panicModeActivated

    fun updateHandle(newHandle: String) {
        if (newHandle.isBlank()) {
            _handleUpdateResult.value = HandleUpdateResult.Error("Handle cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                val isAvailable = userRepository.isHandleAvailable(newHandle)
                if (!isAvailable) {
                    _handleUpdateResult.value = HandleUpdateResult.Error("Handle is already taken")
                    return@launch
                }

                userRepository.updateUserHandle(newHandle)
                _handleUpdateResult.value = HandleUpdateResult.Success
            } catch (e: Exception) {
                _handleUpdateResult.value = HandleUpdateResult.Error("Failed to update handle")
            }
        }
    }

    fun updateAvatar(avatarId: Int) {
        viewModelScope.launch {
            userRepository.updateUserAvatar(avatarId)
        }
    }

    fun toggle2FA(enabled: Boolean) {
        viewModelScope.launch {
            // Implement 2FA toggle logic
            _is2FAEnabled.value = enabled
        }
    }

    fun activatePanicMode(password: String) {
        viewModelScope.launch {
            // Implement panic mode - encrypt all messages with password
            _panicModeActivated.value = true
        }
    }

    sealed class HandleUpdateResult {
        object Success : HandleUpdateResult()
        data class Error(val message: String) : HandleUpdateResult()
    }
}