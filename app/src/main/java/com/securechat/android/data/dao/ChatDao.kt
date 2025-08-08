package com.securechat.android.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.securechat.android.data.model.Chat

@Dao
interface ChatDao {

    @Query("SELECT * FROM chats ORDER BY lastMessageTime DESC")
    fun getAllChats(): LiveData<List<Chat>>

    @Query("SELECT * FROM chats WHERE id = :chatId")
    suspend fun getChatById(chatId: String): Chat?

    @Query("SELECT * FROM chats WHERE participantId = :participantId LIMIT 1")
    suspend fun getChatWithUser(participantId: String): Chat?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: Chat)

    @Update
    suspend fun updateChat(chat: Chat)

    @Delete
    suspend fun deleteChat(chat: Chat)

    @Query("UPDATE chats SET unreadCount = 0 WHERE id = :chatId")
    suspend fun markChatAsRead(chatId: String)
}