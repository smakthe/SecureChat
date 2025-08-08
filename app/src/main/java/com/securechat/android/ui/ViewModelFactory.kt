package com.securechat.android.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.securechat.android.data.repository.ChatRepository
import com.securechat.android.data.repository.MessageRepository
import com.securechat.android.data.repository.UserRepository
import com.securechat.android.security.TwoFactorAuthManager
import com.securechat.android.ui.chat.ChatDetailViewModel
import com.securechat.android.ui.chat.ChatListViewModel
import com.securechat.android.ui.settings.SettingsViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val twoFactorAuthManager: TwoFactorAuthManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            MainViewModel::class.java -> MainViewModel(userRepository, messageRepository) as T
            ChatListViewModel::class.java -> ChatListViewModel(chatRepository, userRepository) as T
            ChatDetailViewModel::class.java -> ChatDetailViewModel(messageRepository, chatRepository, userRepository) as T
            SettingsViewModel::class.java -> SettingsViewModel(userRepository, twoFactorAuthManager) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}