package com.securechat.android.di

import android.content.Context
import androidx.room.Room
import com.securechat.android.data.database.SecureChatDatabase
import com.securechat.android.data.repository.ChatRepository
import com.securechat.android.data.repository.MessageRepository
import com.securechat.android.data.repository.UserRepository
import com.securechat.android.security.EncryptionManager
import com.securechat.android.security.TwoFactorAuthManager

class AppContainer(private val context: Context) {
    
    private val database by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            SecureChatDatabase::class.java,
            "secure_chat_database"
        ).build()
    }
    
    val encryptionManager by lazy { EncryptionManager(context) }
    val twoFactorAuthManager by lazy { TwoFactorAuthManager() }
    
    val userRepository by lazy { 
        UserRepository(database.userDao(), encryptionManager) 
    }
    
    val messageRepository by lazy { 
        MessageRepository(database.messageDao(), encryptionManager) 
    }
    
    val chatRepository by lazy { 
        ChatRepository(database.chatDao(), database.messageDao(), encryptionManager) 
    }
}