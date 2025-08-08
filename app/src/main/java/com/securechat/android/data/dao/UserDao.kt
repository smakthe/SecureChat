package com.securechat.android.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.securechat.android.data.model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE isCurrentUser = 1 LIMIT 1")
    suspend fun getCurrentUser(): User?

    @Query("SELECT * FROM users WHERE isCurrentUser = 1 LIMIT 1")
    fun getCurrentUserLiveData(): LiveData<User?>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): User?

    @Query("SELECT * FROM users WHERE handle = :handle AND isCurrentUser = 0")
    suspend fun getUserByHandle(handle: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)
}