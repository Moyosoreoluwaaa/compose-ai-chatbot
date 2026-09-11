package com.example.aichatbot.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val entityId: String,
    val entityType: String, // MESSAGE, SESSION
    val operation: String, // CREATE, UPDATE, DELETE
    val payload: String, // JSON
    val createdAt: Long,
    val retryCount: Int = 0,
    val lastError: String? = null
)
