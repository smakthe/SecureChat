package com.securechat.android.data.repository

import androidx.lifecycle.LiveData
import com.securechat.android.data.dao.MessageDao
import com.securechat.android.data.model.ChatMessage
import com.securechat.android.security.EncryptionManager
import java.util.*

class MessageRepository(
    private val messageDao: MessageDao,
    private val encryptionManager: EncryptionManager
) {

    fun getMessagesForChat(chatId: String): LiveData<List<ChatMessage>> =
        messageDao.getMessagesForChat(chatId)

    suspend fun getLastMessageForChat(chatId: String): ChatMessage? =
        messageDao.getLastMessageForChat(chatId)

    suspend fun sendMessage(
        chatId: String,
        senderId: String,
        recipientId: String,
        content: String,
        messageType: com.securechat.android.data.model.MessageType
    ): ChatMessage {
        // Encrypt message content
        val encryptedContent = encryptMessage(content, recipientId)

        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            senderId = senderId,
            recipientId = recipientId,
            encryptedContent = encryptedContent,
            messageType = messageType,
            timestamp = System.currentTimeMillis()
        )

        messageDao.insertMessage(message)
        return message
    }

    suspend fun saveMessage(message: ChatMessage) {
        messageDao.insertMessage(message)
    }

    suspend fun decryptMessage(message: ChatMessage): String {
        return try {
            decryptMessageContent(message.encryptedContent, message.senderId)
        } catch (e: Exception) {
            "[Decryption Error]"
        }
    }

    private suspend fun encryptMessage(content: String, recipientId: String): String {
        // This would integrate with Signal Protocol
        // For now, using basic AES encryption
        val encrypted = encryptionManager.encryptData(content.toByteArray())
        return "${encrypted.ciphertext.joinToString(",")};${encrypted.iv.joinToString(",")}"
    }

    private suspend fun decryptMessageContent(encryptedContent: String, senderId: String): String {
        try {
            val parts = encryptedContent.split(";")
            val ciphertext = parts[0].split(",").map { it.toByte() }.toByteArray()
            val iv = parts[1].split(",").map { it.toByte() }.toByteArray()

            val encryptedData = EncryptionManager.EncryptedData(ciphertext, iv)
            val decryptedBytes = encryptionManager.decryptData(encryptedData)
            return String(decryptedBytes)
        } catch (e: Exception) {
            throw Exception("Failed to decrypt message")
        }
    }

    suspend fun encryptAllMessages(password: String) {
        // Panic button functionality - encrypt all messages with password
        // In a real implementation, this would:
        // 1. Get all messages from database
        // 2. Decrypt them with current keys
        // 3. Re-encrypt with panic password
        // 4. Update database with new encrypted content
        
        // For now, we'll simulate the panic mode activation
        // In production, this would use a different encryption key derived from the password
    }
}