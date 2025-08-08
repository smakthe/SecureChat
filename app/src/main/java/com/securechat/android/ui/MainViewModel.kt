package com.securechat.android.ui

import androidx.lifecycle.*
import com.securechat.android.data.model.User
import com.securechat.android.data.repository.MessageRepository
import com.securechat.android.data.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    val currentUser: LiveData<User?> = userRepository.getCurrentUser()

    private val _panicModeActivated = MutableLiveData<Boolean>()
    val panicModeActivated: LiveData<Boolean> = _panicModeActivated

    fun activatePanicMode(password: String) {
        viewModelScope.launch {
            messageRepository.encryptAllMessages(password)
            _panicModeActivated.value = true
        }
    }
}