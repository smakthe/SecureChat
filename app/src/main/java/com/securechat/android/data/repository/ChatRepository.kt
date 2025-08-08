package com.securechat.android.data.repository

import androidx.lifecycle.LiveData
import com.securechat.android.data.dao.ChatDao
import com.securechat.android.data.dao.MessageDao
import com.securechat.android.data.model.Chat
import com.securechat.android.data.model.ChatMessage
import com.securechat.android.security.EncryptionManager
class ChatRepository(
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    private val encryptionManager: EncryptionManager
) {

    fun getAllChats(): LiveData<List<Chat>> = chatDao.getAllChats()

    suspend fun getChatById(chatId: String): Chat? = chatDao.getChatById(chatId)

    suspend fun getChatWithUser(participantId: String): Chat? = chatDao.getChatWithUser(participantId)

    suspend fun saveChat(chat: Chat) {
        chatDao.insertChat(chat)
    }

    suspend fun updateChat(chat: Chat) {
        chatDao.updateChat(chat)
    }

    suspend fun deleteChat(chat: Chat) {
        messageDao.deleteAllMessagesForChat(chat.id)
        chatDao.deleteChat(chat)
    }

    suspend fun markChatAsRead(chatId: String, currentUserId: String) {
        chatDao.markChatAsRead(chatId)
        messageDao.markMessagesAsRead(chatId, currentUserId)
    }
}