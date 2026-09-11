package com.example.aichatbot.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("sessionId"),
        Index("createdAt")
    ]
)
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val sessionId: String,
    val content: String,
    val role: String, // USER, ASSISTANT, SYSTEM
    val timestamp: Long,
    val sourceReferences: String? = null, // JSON string
    val isLoading: Boolean = false,
    val error: String? = null,
    val cloudSynced: Boolean = false,
    val lastSyncTime: Long? = null
)
