package com.securechat.android.data.repository

import androidx.lifecycle.LiveData
import com.securechat.android.data.dao.UserDao
import com.securechat.android.data.model.User
import com.securechat.android.security.EncryptionManager
class UserRepository(
    private val userDao: UserDao,
    private val encryptionManager: EncryptionManager
) {

    fun getCurrentUser(): LiveData<User?> = userDao.getCurrentUserLiveData()

    suspend fun getCurrentUserSync(): User? = userDao.getCurrentUser()

    suspend fun getUserById(userId: String): User? = userDao.getUserById(userId)

    suspend fun getUserByHandle(handle: String): User? = userDao.getUserByHandle(handle)

    suspend fun saveUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun updateUserHandle(newHandle: String) {
        val currentUser = getCurrentUserSync()
        currentUser?.let { user ->
            val updatedUser = user.copy(handle = newHandle)
            updateUser(updatedUser)
        }
    }

    suspend fun updateUserAvatar(avatarId: Int) {
        val currentUser = getCurrentUserSync()
        currentUser?.let { user ->
            val updatedUser = user.copy(avatarId = avatarId)
            updateUser(updatedUser)
        }
    }

    suspend fun isHandleAvailable(handle: String): Boolean {
        return getUserByHandle(handle) == null
    }
}