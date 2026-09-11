package com.example.aichatbot.shared.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import co.touchlab.kermit.Logger

class GeminiApiClient(
    private val httpClient: HttpClient,
    private val apiKey: String
) {
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models"
    private val model = "gemini-pro"
    private val logger = Logger
    
    suspend fun generateContent(prompt: String): GeminiResponse {
        return try {
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(text = prompt)
                        )
                    )
                )
            )
            
            val response = httpClient.post(
                "$baseUrl/$model:generateContent?key=$apiKey"
            ) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<GeminiResponse>()
            
            logger.d { "Gemini response received" }
            response
        } catch (e: Exception) {
            logger.e { "Gemini API error: ${e.message}" }
            throw e
        }
    }
}

@Serializable
data class GeminiRequest(
    val contents: List<Content>
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>,
    val usageMetadata: UsageMetadata
)

@Serializable
data class Candidate(
    val content: Content,
    val finishReason: String? = null
)

@Serializable
data class UsageMetadata(
    val promptTokenCount: Int = 0,
    val candidatesTokenCount: Int = 0,
    val totalTokenCount: Int = 0
)
