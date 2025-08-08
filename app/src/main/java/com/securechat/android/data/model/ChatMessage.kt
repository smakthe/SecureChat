package com.securechat.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class ChatMessage(
    @PrimaryKey
    val id: String,
    val chatId: String,
    val senderId: String,
    val recipientId: String,
    val encryptedContent: String,
    val messageType: MessageType,
    val timestamp: Long,
    val isRead: Boolean = false,
    val filePath: String? = null,
    val fileSize: Long? = null
)

enum class MessageType {
    TEXT,
    FILE,
    IMAGE
}