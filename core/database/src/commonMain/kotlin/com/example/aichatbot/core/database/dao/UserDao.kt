package com.example.aichatbot.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aichatbot.core.database.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUser(userId: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE googleId = :googleId LIMIT 1")
    suspend fun getUserByGoogleId(googleId: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE isActive = 1 LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?
    
    @Query("UPDATE users SET accessToken = :accessToken, tokenExpiresAt = :expiresAt WHERE id = :userId")
    suspend fun updateAccessToken(userId: String, accessToken: String, expiresAt: Long)
    
    @Query("UPDATE users SET lastSignIn = :timestamp WHERE id = :userId")
    suspend fun updateLastSignIn(userId: String, timestamp: Long)
}
