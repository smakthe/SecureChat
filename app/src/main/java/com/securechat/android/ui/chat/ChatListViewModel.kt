package com.securechat.android.ui.chat

import androidx.lifecycle.*
import com.securechat.android.data.model.Chat
import com.securechat.android.data.model.User
import com.securechat.android.data.repository.ChatRepository
import com.securechat.android.data.repository.UserRepository
import kotlinx.coroutines.launch

class ChatListViewModel(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val chats: LiveData<List<Chat>> = chatRepository.getAllChats()
    val currentUser: LiveData<User?> = userRepository.getCurrentUser()

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _navigateToChat = MutableLiveData<Pair<String, String>?>()
    val navigateToChat: LiveData<Pair<String, String>?> = _navigateToChat

    fun refreshChats() {
        viewModelScope.launch {
            _isRefreshing.value = true
            // Implement refresh logic here
            // This would typically sync with server
            _isRefreshing.value = false
        }
    }

    fun onChatClicked(chat: Chat) {
        viewModelScope.launch {
            val participant = userRepository.getUserById(chat.participantId)
            participant?.let {
                _navigateToChat.value = chat.id to it.handle
            }
        }
    }

    fun onNavigatedToChat() {
        _navigateToChat.value = null
    }

    fun markChatAsRead(chatId: String) {
        viewModelScope.launch {
            val currentUserId = userRepository.getCurrentUserSync()?.id ?: return@launch
            chatRepository.markChatAsRead(chatId, currentUserId)
        }
    }
    
    fun startNewChat(handle: String) {
        viewModelScope.launch {
            val user = userRepository.getUserByHandle(handle)
            if (user != null) {
                val existingChat = chatRepository.getChatWithUser(user.id)
                if (existingChat != null) {
                    _navigateToChat.value = existingChat.id to user.handle
                } else {
                    val newChat = com.securechat.android.data.model.Chat(
                        id = java.util.UUID.randomUUID().toString(),
                        participantId = user.id,
                        lastMessage = "",
                        lastMessageTime = System.currentTimeMillis()
                    )
                    chatRepository.saveChat(newChat)
                    _navigateToChat.value = newChat.id to user.handle
                }
            }
        }
    }
    
    fun deleteChat(chat: com.securechat.android.data.model.Chat) {
        viewModelScope.launch {
            chatRepository.deleteChat(chat)
        }
    }
}