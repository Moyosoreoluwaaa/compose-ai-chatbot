package com.example.aichatbot.shared.data.repository

import com.example.aichatbot.shared.data.remote.GeminiApiClient
import com.example.aichatbot.shared.domain.model.AIResponse
import com.example.aichatbot.shared.domain.model.ChatMessage
import com.example.aichatbot.shared.domain.model.MessageRole
import com.example.aichatbot.shared.domain.model.SourceReference
import com.example.aichatbot.shared.domain.model.SourceMetadata
import com.example.aichatbot.shared.domain.repository.AIRepository
import co.touchlab.kermit.Logger

class AIRepositoryImpl(
    private val geminiClient: GeminiApiClient,
    private val logger: Logger
) : AIRepository {
    override suspend fun sendMessage(prompt: String, sessionId: String): AIResponse {
        return try {
            logger.d { "Sending message to Gemini API: $prompt" }
            
            val response = geminiClient.generateContent(prompt)
            
            val sources = response.candidates.firstOrNull()
                ?.content?.parts?.firstOrNull()
                ?.let { extractSourcesFromResponse(it.text) }
                ?: emptyList()
            
            AIResponse(
                id = generateId(),
                content = response.candidates.firstOrNull()
                    ?.content?.parts?.firstOrNull()?.text ?: "No response",
                sources = sources,
                model = "gemini-pro",
                tokens = com.example.aichatbot.shared.domain.model.TokenUsage(
                    promptTokens = response.usageMetadata.promptTokenCount,
                    completionTokens = response.usageMetadata.candidatesTokenCount,
                    totalTokens = response.usageMetadata.totalTokenCount
                )
            )
        } catch (e: Exception) {
            logger.e { "Error calling Gemini API: ${e.message}" }
            throw e
        }
    }
    
    override suspend fun generateTitle(messages: List<ChatMessage>): String {
        return try {
            val messagesText = messages.take(3).joinToString("\n") { it.content }
            val response = geminiClient.generateContent(
                "Generate a short, descriptive title (max 50 chars) for this conversation:\n$messagesText"
            )
            response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "New Chat"
        } catch (e: Exception) {
            logger.e { "Error generating title: ${e.message}" }
            "New Chat"
        }
    }
    
    private fun extractSourcesFromResponse(text: String): List<SourceReference> {
        val sources = mutableListOf<SourceReference>()
        // Simple pattern to extract URLs and text references
        val urlPattern = Regex("https?://[^\\s]+")
        var citationIndex = 1
        
        urlPattern.findAll(text).forEach { match ->
            val url = match.value.trim(')')
            sources.add(
                SourceReference(
                    id = generateId(),
                    title = "Source ${citationIndex}",
                    url = url,
                    snippet = url,
                    domain = url.split("/")[2],
                    citationIndex = citationIndex,
                    metadata = SourceMetadata()
                )
            )
            citationIndex++
        }
        
        return sources
    }
    
    private fun generateId(): String = java.util.UUID.randomUUID().toString()
}
