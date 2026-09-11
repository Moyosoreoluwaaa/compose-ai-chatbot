package com.example.aichatbot.core.database.repository

import com.example.aichatbot.shared.domain.model.ChatMessage
import com.example.aichatbot.shared.domain.model.ChatSession
import com.example.aichatbot.shared.domain.model.MessageRole
import com.example.aichatbot.shared.domain.repository.ChatRepository
import com.example.aichatbot.core.database.AppDatabase
import com.example.aichatbot.core.database.entity.ChatMessageEntity
import com.example.aichatbot.core.database.entity.ChatSessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import co.touchlab.kermit.Logger

class ChatRepositoryImpl(
    private val database: AppDatabase,
    private val logger: Logger
) : ChatRepository {
    private val json = Json { ignoreUnknownKeys = true }
    
    override suspend fun createSession(title: String): ChatSession {
        val sessionId = java.util.UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val session = ChatSessionEntity(
            id = sessionId,
            title = title,
            userId = getCurrentUserId(),
            createdAt = now,
            updatedAt = now
        )
        database.chatSessionDao().insertSession(session)
        logger.d { "Created session: $sessionId" }
        return session.toDomain()
    }
    
    override suspend fun getSession(sessionId: String): ChatSession? {
        return database.chatSessionDao().getSession(sessionId)?.toDomain()
    }
    
    override suspend fun getAllSessions(): List<ChatSession> {
        return database.chatSessionDao()
            .observeUserSessions(getCurrentUserId())
            .map { it.map { entity -> entity.toDomain() } }
            .let { flow ->
                var result = emptyList<ChatSession>()
                flow.collect { result = it }
                result
            }
    }
    
    override suspend fun deleteSession(sessionId: String) {
        val session = database.chatSessionDao().getSession(sessionId) ?: return
        database.chatSessionDao().deleteSession(session)
        database.chatMessageDao().deleteSessionMessages(sessionId)
        logger.d { "Deleted session: $sessionId" }
    }
    
    override suspend fun addMessage(sessionId: String, message: ChatMessage): ChatSession {
        val messageEntity = message.toEntity(sessionId)
        database.chatMessageDao().insertMessage(messageEntity)
        
        // Update session timestamp
        val session = database.chatSessionDao().getSession(sessionId)?.copy(
            updatedAt = System.currentTimeMillis()
        ) ?: return ChatSession(sessionId, "Unknown")
        database.chatSessionDao().updateSession(session)
        
        logger.d { "Added message to session: $sessionId" }
        return session.toDomain()
    }
    
    override suspend fun updateSessionTitle(sessionId: String, title: String) {
        val session = database.chatSessionDao().getSession(sessionId) ?: return
        database.chatSessionDao().updateSession(
            session.copy(title = title, updatedAt = System.currentTimeMillis())
        )
        logger.d { "Updated session title: $sessionId" }
    }
    
    override suspend fun archiveSession(sessionId: String) {
        val session = database.chatSessionDao().getSession(sessionId) ?: return
        database.chatSessionDao().updateSession(
            session.copy(isArchived = true, updatedAt = System.currentTimeMillis())
        )
        logger.d { "Archived session: $sessionId" }
    }
    
    override fun observeSessionMessages(sessionId: String): Flow<List<ChatMessage>> {
        return database.chatMessageDao().observeSessionMessages(sessionId)
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    override fun observeAllSessions(): Flow<List<ChatSession>> {
        return database.chatSessionDao()
            .observeActiveSessions(getCurrentUserId())
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    private fun getCurrentUserId(): String {
        // This would be replaced with actual auth system
        return "user_default"
    }
    
    private fun ChatSessionEntity.toDomain(): ChatSession {
        return ChatSession(
            id = id,
            title = title,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isArchived = isArchived
        )
    }
    
    private fun ChatMessageEntity.toDomain(): ChatMessage {
        return ChatMessage(
            id = id,
            content = content,
            role = MessageRole.valueOf(role),
            timestamp = timestamp,
            sourceReferences = sourceReferences?.let {
                try {
                    json.decodeFromString(it)
                } catch (e: Exception) {
                    emptyList()
                }
            } ?: emptyList(),
            isLoading = isLoading,
            error = error
        )
    }
    
    private fun ChatMessage.toEntity(sessionId: String): ChatMessageEntity {
        return ChatMessageEntity(
            id = id,
            sessionId = sessionId,
            content = content,
            role = role.name,
            timestamp = timestamp,
            sourceReferences = if (sourceReferences.isNotEmpty()) {
                json.encodeToString(sourceReferences)
            } else null,
            isLoading = isLoading,
            error = error
        )
    }
}
