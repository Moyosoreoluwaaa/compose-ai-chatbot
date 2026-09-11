package com.example.aichatbot.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aichatbot.core.database.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)
    
    @Update
    suspend fun updateMessage(message: ChatMessageEntity)
    
    @Delete
    suspend fun deleteMessage(message: ChatMessageEntity)
    
    @Query("SELECT * FROM chat_messages WHERE id = :messageId LIMIT 1")
    suspend fun getMessage(messageId: String): ChatMessageEntity?
    
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun observeSessionMessages(sessionId: String): Flow<List<ChatMessageEntity>>
    
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getSessionMessagesPaginated(
        sessionId: String,
        limit: Int,
        offset: Int
    ): List<ChatMessageEntity>
    
    @Query("SELECT COUNT(*) FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun getMessageCount(sessionId: String): Int
    
    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun deleteSessionMessages(sessionId: String)
    
    @Query("SELECT * FROM chat_messages WHERE cloudSynced = 0 LIMIT 50")
    suspend fun getUnsyncedMessages(): List<ChatMessageEntity>
    
    @Query("UPDATE chat_messages SET cloudSynced = 1, lastSyncTime = :syncTime WHERE id = :messageId")
    suspend fun markMessageSynced(messageId: String, syncTime: Long)
    
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId AND content LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchMessages(sessionId: String, query: String): Flow<List<ChatMessageEntity>>
}
