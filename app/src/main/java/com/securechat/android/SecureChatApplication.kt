package com.securechat.android

import android.app.Application
import com.securechat.android.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SecureChatApplication : Application() {
    val appContainer by lazy { AppContainer(this) }
    
    override fun onCreate() {
        super.onCreate()
        initializeSampleData()
    }
    
    private fun initializeSampleData() {
        CoroutineScope(Dispatchers.IO).launch {
            val currentUser = appContainer.userRepository.getCurrentUserSync()
            if (currentUser == null) {
                // Create default user
                val defaultUser = com.securechat.android.data.model.User(
                    id = java.util.UUID.randomUUID().toString(),
                    phoneNumber = "+1234567890",
                    handle = "user${System.currentTimeMillis().toString().takeLast(4)}",
                    avatarId = 1,
                    publicKey = "sample_public_key",
                    isCurrentUser = true
                )
                appContainer.userRepository.saveUser(defaultUser)
                
                // Create some sample contacts
                val sampleUsers = listOf(
                    com.securechat.android.data.model.User(
                        id = java.util.UUID.randomUUID().toString(),
                        phoneNumber = "+1234567891",
                        handle = "alice",
                        avatarId = 2,
                        publicKey = "alice_public_key"
                    ),
                    com.securechat.android.data.model.User(
                        id = java.util.UUID.randomUUID().toString(),
                        phoneNumber = "+1234567892",
                        handle = "bob",
                        avatarId = 3,
                        publicKey = "bob_public_key"
                    )
                )
                
                sampleUsers.forEach { user ->
                    appContainer.userRepository.saveUser(user)
                    
                    // Create sample chat
                    val chat = com.securechat.android.data.model.Chat(
                        id = java.util.UUID.randomUUID().toString(),
                        participantId = user.id,
                        lastMessage = "Hey there! 👋",
                        lastMessageTime = System.currentTimeMillis() - (1..1000000).random()
                    )
                    appContainer.chatRepository.saveChat(chat)
                }
            }
        }
    }
}