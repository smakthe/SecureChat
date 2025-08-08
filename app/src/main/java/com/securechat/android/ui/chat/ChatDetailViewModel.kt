package com.securechat.android.ui.chat

import androidx.lifecycle.*
import com.securechat.android.data.model.ChatMessage
import com.securechat.android.data.model.MessageType
import com.securechat.android.data.model.User
import com.securechat.android.data.repository.ChatRepository
import com.securechat.android.data.repository.MessageRepository
import com.securechat.android.data.repository.UserRepository
import kotlinx.coroutines.launch

class ChatDetailViewModel(
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _chatId = MutableLiveData<String>()
    val chatId: LiveData<String> = _chatId

    private val _participant = MutableLiveData<User?>()
    val participant: LiveData<User?> = _participant

    val messages: LiveData<List<ChatMessage>> = _chatId.switchMap { chatId ->
        messageRepository.getMessagesForChat(chatId)
    }

    val currentUser: LiveData<User?> = userRepository.getCurrentUser()

    private val _isTyping = MutableLiveData<Boolean>(false)
    val isTyping: LiveData<Boolean> = _isTyping

    private val _messageText = MutableLiveData<String>()
    val messageText: LiveData<String> = _messageText

    fun setChatId(chatId: String) {
        _chatId.value = chatId
    }

    fun setParticipantHandle(handle: String) {
        viewModelScope.launch {
            val user = userRepository.getUserByHandle(handle)
            _participant.value = user
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            val currentUserId = userRepository.getCurrentUserSync()?.id ?: return@launch
            val participantId = _participant.value?.id ?: return@launch
            val chatId = _chatId.value ?: return@launch

            messageRepository.sendMessage(
                chatId = chatId,
                senderId = currentUserId,
                recipientId = participantId,
                content = content,
                messageType = MessageType.TEXT
            )

            _messageText.value = ""
        }
    }

    fun onMessageTextChanged(text: String) {
        _messageText.value = text
        
        // Simple typing indicator simulation
        if (text.isNotEmpty() && _isTyping.value != true) {
            _isTyping.value = true
            // In a real app, this would send typing status to other user
        } else if (text.isEmpty() && _isTyping.value == true) {
            _isTyping.value = false
        }
    }

    fun markMessagesAsRead() {
        viewModelScope.launch {
            val currentUserId = userRepository.getCurrentUserSync()?.id ?: return@launch
            val chatId = _chatId.value ?: return@launch
            chatRepository.markChatAsRead(chatId, currentUserId)
        }
    }

    suspend fun decryptMessage(message: ChatMessage): String {
        return messageRepository.decryptMessage(message)
    }
}