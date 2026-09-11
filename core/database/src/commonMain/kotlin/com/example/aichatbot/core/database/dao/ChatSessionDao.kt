package com.example.aichatbot.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aichatbot.core.database.entity.ChatSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSessionEntity)
    
    @Update
    suspend fun updateSession(session: ChatSessionEntity)
    
    @Delete
    suspend fun deleteSession(session: ChatSessionEntity)
    
    @Query("SELECT * FROM chat_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSession(sessionId: String): ChatSessionEntity?
    
    @Query("SELECT * FROM chat_sessions WHERE userId = :userId ORDER BY updatedAt DESC")
    fun observeUserSessions(userId: String): Flow<List<ChatSessionEntity>>
    
    @Query("SELECT * FROM chat_sessions WHERE userId = :userId AND isArchived = 0 ORDER BY updatedAt DESC")
    fun observeActiveSessions(userId: String): Flow<List<ChatSessionEntity>>
    
    @Query("SELECT * FROM chat_sessions WHERE cloudSynced = 0 LIMIT 50")
    suspend fun getUnsyncedSessions(): List<ChatSessionEntity>
    
    @Query("UPDATE chat_sessions SET cloudSynced = 1, lastSyncTime = :syncTime WHERE id = :sessionId")
    suspend fun markSessionSynced(sessionId: String, syncTime: Long)
}
