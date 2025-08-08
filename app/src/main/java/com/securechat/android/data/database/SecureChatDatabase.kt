package com.securechat.android.data.database

import androidx.room.*
import com.securechat.android.data.dao.*
import com.securechat.android.data.model.*

@Database(
    entities = [
        User::class,
        ChatMessage::class,
        Chat::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SecureChatDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao
    abstract fun chatDao(): ChatDao
}