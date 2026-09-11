package com.example.aichatbot.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class VoiceInput(
    val id: String,
    val text: String,
    val duration: Long, // milliseconds
    val confidence: Float, // 0.0 - 1.0
    val language: String = "en-US",
    val timestamp: Long
)
