package com.example.aichatbot.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aichatbot.core.database.entity.SyncQueueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncQueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncItem(item: SyncQueueEntity): Long
    
    @Delete
    suspend fun deleteSyncItem(item: SyncQueueEntity)
    
    @Query("SELECT * FROM sync_queue ORDER BY createdAt ASC LIMIT :limit")
    suspend fun getPendingSyncItems(limit: Int = 50): List<SyncQueueEntity>
    
    @Query("SELECT COUNT(*) FROM sync_queue")
    fun observePendingCount(): Flow<Int>
    
    @Query("SELECT * FROM sync_queue WHERE entityId = :entityId AND entityType = :entityType")
    suspend fun getSyncItemsForEntity(entityId: String, entityType: String): List<SyncQueueEntity>
    
    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun deleteSyncItemById(id: Int)
    
    @Query("UPDATE sync_queue SET retryCount = retryCount + 1, lastError = :error WHERE id = :id")
    suspend fun incrementRetryCount(id: Int, error: String)
}
