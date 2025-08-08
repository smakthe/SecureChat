package com.securechat.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val phoneNumber: String,
    val handle: String,
    val avatarId: Int,
    val publicKey: String,
    val isCurrentUser: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)