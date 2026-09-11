package com.example.aichatbot.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.aichatbot.core.database.dao.ChatMessageDao
import com.example.aichatbot.core.database.dao.ChatSessionDao
import com.example.aichatbot.core.database.dao.SyncQueueDao
import com.example.aichatbot.core.database.dao.UserDao
import com.example.aichatbot.core.database.entity.ChatMessageEntity
import com.example.aichatbot.core.database.entity.ChatSessionEntity
import com.example.aichatbot.core.database.entity.SyncQueueEntity
import com.example.aichatbot.core.database.entity.UserEntity

@Database(
    entities = [
        ChatSessionEntity::class,
        ChatMessageEntity::class,
        UserEntity::class,
        SyncQueueEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatSessionDao(): ChatSessionDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun userDao(): UserDao
    abstract fun syncQueueDao(): SyncQueueDao
}
